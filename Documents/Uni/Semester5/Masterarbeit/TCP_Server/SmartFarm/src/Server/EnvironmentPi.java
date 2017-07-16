package Server;

import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class EnvironmentPi is designed for the communication with the EinvironmentPi's
 * For each EnvironmentPi that registers a new object is created
 * Every object means a new thread, that is why the class Runnable is implemented
 * 
 * @author Jasmin
 *
 */
public class EnvironmentPi implements Runnable{
	
	/* 
	 * maxTemp is the maximum temperature that is accepted in the stable
	 * minTemp is the minimum temperature that is accepted in the stable
	 * If the temperature is higher than maxTemp air condition is turned on
	 * If the temperature is lower than minTemp heating is turned on
	 * tolerance is the difference between the temperature at which the heating/air condition shall be
	 * turned on and the temperature at which it shall be turned off again.
	 * Declare the tolerance, minimum and maximum temperature as static, so that they can be accessed from other classes 
	 */
	public static float maxTemp;	
    public static float minTemp;		
    public static double tolerance;	
    int databaseStoreFrequency = 5000;
	Socket environmentSocket;
	public PrintWriter environmentCameraRequestStream;
	public String environmentID;
	public int stableID;
	String tempstr;
	String humistr;
	// HashMap to save the EnvironmentPi objects (necessary to get the output stream, where the message for the camera live feed must be sent)
	public static HashMap<String, EnvironmentPi> map = new HashMap<String, EnvironmentPi>();
	
    
	/**
	 * Constructor
	 * @param environmentSocket Socket that shall be used for the communication between the EnvironmentPi and the work station
	 */
	public EnvironmentPi(Socket environmentSocket) {
		 this.environmentSocket = environmentSocket;
	}
    
	/**
	 * Run method of the EnvironmentPi-Thread, enables communication between work station and EnvironmentPi
	 */
	public void run() {
		try {
			boolean run = true;
			
			/* Create BufferedReader and PrintWriter to enable TCP-communication between Work Station and EnvironmentPi */
			environmentCameraRequestStream = new PrintWriter(environmentSocket.getOutputStream(),true);
			BufferedReader environmentInputStream = new BufferedReader(new InputStreamReader(environmentSocket.getInputStream()));
			PrintWriter environmentOutputStream = new PrintWriter(environmentSocket.getOutputStream(),true);
		
			/* Receive first message from the EnvironmentPi, which includes only the EnvironmentID (= MAC-Address of the Raspi) */
			String clientMessage = environmentInputStream.readLine();
			this.environmentID = clientMessage;
			
			/* Add current object to the HashMap of EnvironmentPi objects */
			map.put(this.environmentID, this);
			
			/* Check if EnvironmentID is already registered in the Database */
			if (!(DatabaseConnection.isStableRegistered(environmentID))) {

				/* If not: Open Stable Registration Window, so the user can register the EnvironmentID with an StableID in the Database */
				String stableIDstring = StableRegistrationWindow.display();
				
				/* Check if registration was successful (= check if the user entered an StableID and clicked OK) */
				boolean regSuccess = DatabaseConnection.registerStable(environmentID, stableIDstring);
				if (regSuccess == false) {
					
					/* If not: Close the connection to the EnvironmentPi and end the thread */
					ClientResponder.sendToClient("disconnect", environmentOutputStream);
					run = false;
				} else {
					
					/* If yes: Send acknowledgement to the EnvironmentPi */
					ClientResponder.sendToClient("ack", environmentOutputStream);
					
				}
				
			}
			
			else {
				/* If EnvironmentID is already registered in the database just send acknowledgement to the EnvironmentPi */
				ClientResponder.sendToClient("ack", environmentOutputStream);
			}
			
			/* Get the StableID from the database */
			stableID = DatabaseConnection.getStableID(environmentID);
			
			/* Add StableID to the ComboBox for Stables within the GUI */
			String stableIDstring = Integer.toString(stableID);
			GUIWorkStation.comboBoxStableNumber.addItem(stableIDstring);
			
			/* Define tempCounter for counting the temperature measurements, because only if a certain number of 
			 * temperature measurements is done, the temperature will be stored in the database
			 * Set it to databaseStoreFrequency, so that the first measured value will be stored
			 */
			int tempCounter = databaseStoreFrequency;
			
			/* As long as the EnvironmentPi is not disconnecting and everything works, this loop is executed */
			while (run) {
				
				/* Receive message from client and part the string into its components */
				String[] arrayFromEnviron = ClientResponder.resolveStringToArray(environmentInputStream.readLine());
            	
            	/* The first part is the wearableID (= MAC address of the device) */
            	environmentID = arrayFromEnviron[0];
            	
            	/* The second part is designed to send a disconnect statement, if the client hat any reasons to that */
            	if(arrayFromEnviron[1].equals("disconnect")) {
            		environmentSocket.close();
            		System.out.println("Disconnect received. Shut down");
            	}
            	
            	/* Then the rest of the message is evaluated */
            	this.tempstr = arrayFromEnviron[2];
            	float temp = Float.parseFloat(tempstr);
            	String lastTempAction = arrayFromEnviron[3];
            	this.humistr = arrayFromEnviron[4];
            	float humidity = Float.parseFloat(humistr);
            	
            	/* If the selected AnimalID in the comboBox within the GUI is the AnimalID from
				 * the animal of this WearablePI then update the shown body temperature value
				 * within the GUI and replace it with the received values
				 */
            	if (GUIWorkStation.comboBoxStableNumber.getSelectedItem() == stableIDstring) {
            		//double tempTemp = Math.pow((double)temp, 1);
            		//tempstr = String.valueOf(tempTemp);
            		WorkStation.smartFarmGUI.showValuesStable(tempstr, humistr, minTemp, maxTemp);
            	}
            	
            	/* Compute answer that shall be sent to the EnvironmentPi in accordance to the last temperature Action,
            	 * the temperature, maxTemp, minTemp and tolerance. The answer will include an instruction for the
            	 * EnvironmentPi
            	 */
            	String toEnviron = generateAnswerToEnvironmentPi(lastTempAction, temp, maxTemp, minTemp, tolerance);
            	
            	/* Insert room temperature into the database only after a certain number of measurements or if the temperature
            	 * Action changes (if heating/air condition are turned off or on) because it is not 
				 * necessary to save the body temperature five times a second or even more times
				 * 5000 measurements match vaguely 100 seconds
				 */
            	if (toEnviron == "airc" || toEnviron == "heat" || toEnviron == "noheat" || toEnviron == "noairc" || tempCounter == databaseStoreFrequency) {
        			
            		/* Reset counter */
            		tempCounter = 1;
            		
            		/* If air condition or heating shall be turned on, check if the temperature is valid
            		 * (between 0 and 50) because the GUI made a problem instead 
            		 * In all cases save the temperature, humidity and tempAction in the database
            		 */
            		if (toEnviron == "airc" || toEnviron == "heat") {
            			if(temp > 0 && temp < 50) DatabaseConnection.insertDataRoomParameter(environmentID, temp, humidity, toEnviron);
            		} else if (toEnviron == "ack" || toEnviron == "noheat" || toEnviron == "noairc") {
            			DatabaseConnection.insertDataRoomParameter(environmentID, temp, humidity, "null");
            		} else if (toEnviron == "warm") {
            			DatabaseConnection.insertDataRoomParameter(environmentID, temp, humidity, "airc");
            		} else if (toEnviron == "cold") {
            			DatabaseConnection.insertDataRoomParameter(environmentID, temp, humidity, "heat");
            		}
            	
            	/* TempAction did not change and not enough measurements yet */	
            	} else {
            		tempCounter++;
            	}
            	
            	/* Send the computed answer to the EnvironmentPi */
            	ClientResponder.sendToClient(toEnviron, environmentOutputStream);  
            	
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
			 
		try {
			 environmentSocket.close();
		} catch (IOException ex) {
			 Logger.getLogger(WearablePi.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}
	
    
	/**
	 * Generates answer to the EnvironmentPi in accordance to the current TempAction, the temperature, the maximum and minimum 
	 * temperature that is acceptable and the chosen tolerance
	 * 
	 * @param currentAction Current TempAction received from the EnvironmentPi
	 * @param temp Current temperature in the stable
	 * @param max Chosen maximum temperature
	 * @param min Chosen minimum temperature
	 * @param tolerance Difference which determines when the heating/air condition should be turned off again
	 * 
	 * @return String answer that should be sent to client
	 */
    public static String generateAnswerToEnvironmentPi(String currentAction, float temp, float max, float min, double tolerance){
    	String toClient = "";
    	switch (currentAction) {
		case "null": 										// No temperature action before 
			if (temp > max) {
    			toClient = "airc"; 							// Turn air condition on
    			System.out.println("Air condition on"); 
    		}
    		else if (temp <= min) {
    			toClient = "heat"; 							// Turn heating on
    			System.out.println("Heating on");
    		}
    		else {
    			toClient = "ack"; 							// Do nothing
    		}
			break;
		case "heat": 										// Heating before
			if (temp < min + tolerance) {
				toClient = "cold"; 							// Still heating
			}
			else if (temp >= min + tolerance) {
				toClient = "noheat"; 						// Stop heating
				System.out.println("Heating off");
			}
			break;
		case "airc": 										// Air condition before
			if (temp > max - tolerance) {
				toClient = "warm"; 							// Still Air condition
			}
			else if (temp <= max - tolerance) {
				toClient = "noairc"; 						// Stop Air condition 
				System.out.println("Air condition off");
			}
			break;
    	}
    	return toClient;
    }

    /**
     *  Method to get the object of the Environment Pi class
     * @param environmentID The Environment Pi's ID to which the belonging object is needed
     * @return environ Object of the class EnvironmentPi
     */
    public static EnvironmentPi getEnvironmentPi (String environmentID) {
    	EnvironmentPi environ = map.get(environmentID);
    	return environ;
    }
}
