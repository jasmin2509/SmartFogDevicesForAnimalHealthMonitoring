package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * The class WearablePi is designed for the communication with the Wearable R-Pi's
 * For each WearablePi that registers a new object is created
 * Every object means a new thread, that is why the class Runnable is implemented
 * 
 * @author Jasmin
 *
 */
public class WearablePi implements Runnable{
	
	/* declare the maximum body temperature as static, so that it can be accessed from other classes */
	public static double maxBodyTemp;
	Socket wearableSocket;
	String wearableID; // Mac-Address
	public int animalID;
	String serverAnswer = "ack";
	int databaseStoreFrequency = 5000;
	boolean run = true;
	String lastMovement = null;
	
	/**
	 * Constructor
	 * @param wearableSocket Socket that shall be used for the communication between the Wearable R-Pi and the work station
	 */
	public WearablePi(Socket wearableSocket) {
		 this.wearableSocket = wearableSocket;
	}
	
	/**
	 * Run method of the Wearable R-Pi Thread, enables communication between work station and WearablePi
	 */
	public void run() {
		
		try {
			
			/* Specifiy values for the Fast Fourier Transformation that will be made with the movement values
			 * to specify the current movement state
			 */
			int sampleCount = 4096;
			double[] vectorArray = new double[sampleCount];
			double[] globalVectorArray = new double[sampleCount];
			double[] n = new double[sampleCount];
			
			/* Create BufferedReader and PrintWriter to enable TCP-communication between Work Station and WearablePi */
			BufferedReader wearableInputStream = new BufferedReader(new InputStreamReader(wearableSocket.getInputStream()));
			PrintWriter wearableOutputStream = new PrintWriter(wearableSocket.getOutputStream(), true);
			
			/* Receive first message from the WearablePi, which includes only the WearableID (= MAC-Address of the Raspi) */
			String clientMessage = wearableInputStream.readLine();
			wearableID = clientMessage;
			
			/* Check if WearableID is already registered in the Database */
			if (!(DatabaseConnection.isAnimalRegistered(wearableID))) {
				
				/* If not: Open Animal Registration Window, so the user can register the WearableID with an AnimalID in the Database */
				String animalIDstring = AnimalRegistrationWindow.display();
				
				/* Check if registration was successful (= check if the user entered an AnimalID and clicked OK) */
				boolean regSuccess = DatabaseConnection.registerAnimal(wearableID, animalIDstring);
				if (regSuccess == false) {
					
					/* If not: Close the connection to the WearablePi and end the thread */
					ClientResponder.sendToClient("disconnect", wearableOutputStream);
					run = false;
				} else {
					
					/* If yes: Send acknowledgement to the WearablePi */
					ClientResponder.sendToClient("ack", wearableOutputStream);
				}
				
			}
			
			else {
				/* If WearableID is already registered in the database just send acknowledgement to the WearablePi */
				ClientResponder.sendToClient("ack", wearableOutputStream);
			}

			/* Get the AnimalID from the database */
			animalID = DatabaseConnection.getAnimalID(wearableID);
			
			/* Add AnimalID to the ComboBox for Animals within the GUI */
			String animalIDstring = Integer.toString(animalID);
			GUIWorkStation.comboBoxAnimal.addItem(animalIDstring);
			
			/* Define tempCounter for counting the temperature measurements, because only if a certain number of 
			 * body temperature measurements is done, the body temperature will be stored in the database
			 * Set it to databaseStoreFrequency, so that the first measured value will be stored
			 */
			int tempCounter = databaseStoreFrequency;
			
			/* As long as the WearablePi is not disconnecting and everything works, this loop is executed */
			while(run) {
				
			    /* Get the current Systemtime in milliseconds for the FFT */
			    long start = System.currentTimeMillis();
				
		    	/* Repeat the loop until we have enough samples to execute the FFT */
		    	for(int i = 0; i < sampleCount; i++) {
		    		
		    		/* Receive message from client and part the string into its components */
					String[] arrayFromWearable = ClientResponder.resolveStringToArray(wearableInputStream.readLine());
					
					/* The first part is the wearableID (= MAC address of the device) */
					wearableID = arrayFromWearable[0];
					
					/* The second part is designed to send a disconnect statement, if the client hat any reasons to that */
					if(arrayFromWearable[1].equals("disconnect")) {
						wearableSocket.close();
						//System.out.println("Disconnect received. Shut down");
						run = false;
					}
		        	
					/* Then the rest of the message is evaluated */
					String bodytempstr = arrayFromWearable[2]; 								// Part three: body temperature
					double bodytemp = Math.round(Float.parseFloat(bodytempstr)*100)/100.0;	// round body temperature
					double x = Float.parseFloat(arrayFromWearable[3]);						// Part four: movement x-axis
					double y = Float.parseFloat(arrayFromWearable[4]);						// Part five: movement y-axis
					double z = Float.parseFloat(arrayFromWearable[5]);						// Part six: movement z-axis
		        	
					/* Calculate the absolute value of the movement vector (x,y,z) */
					double vector = Math.abs(Math.sqrt((x*x)+(y*y)+(z*z)));
					
					/* Save the absolute value of the vector in an array */
					vectorArray[i] = vector; 	
					n[i] = 0.0;				// needs to be done for FFT
					
					/* If the selected AnimalID in the comboBox within the GUI is the AnimalID from
					 * the animal of this WearablePI then update the shown body temperature value
					 * within the GUI and replace it with the received values
					 */
					if (GUIWorkStation.comboBoxAnimal.getSelectedItem() == animalIDstring) {
						WorkStation.smartFarmGUI.showValuesAnimal(bodytempstr);
					}
					
					/* Insert Body Temperature into the database only after 5000 measurements because it is not 
					 * necessary to save the body temperature five times a second or even more times
					 * 5000 measurements match vaguely 100 seconds
					 */
					if (tempCounter == databaseStoreFrequency) {
						DatabaseConnection.insertDataBodyTemperature(wearableID, bodytemp);
						tempCounter = 1;
					}
					
					else tempCounter++;
					ClientResponder.sendToClient(serverAnswer, wearableOutputStream);
				}
			    	
				
			    /* Calculate the running time out of the stored systemtime and the current */
				long runningTime = (System.currentTimeMillis() - start);
				double runningTimeSeconds = (double)runningTime / 1000;
				System.out.format("Running time: %f%n", runningTimeSeconds);
				
				/* Calculate the samplingRate */
				double samplingRate = sampleCount / (double)runningTimeSeconds; 
				//System.out.format("SamplingRate: %f%n", samplingRate);
				
				/* Execute the FFT to the Array of absolute values of the movement vectors */
				FFT fftObj = new FFT(vectorArray, n);
				fftObj.setSamplingRate(samplingRate);
				//fftObj.printReal();
				fftObj.transform();
				//fftObj.printAmplitude();
				
				
				/* Get maximum value of the amplitude that can be computed from the FFT results */
				double maxAmp = fftObj.getMaxAmplitude();
				System.out.printf("MaxAmp: %f\n", maxAmp);				
				
				/* Evaluate the maximum amplitude to get the current type movement */
				String currentMovement = null;
				if (maxAmp < 0.01) { 				// Animal is lying/sleeping
					currentMovement = "lying";
				} else if (maxAmp < 0.04) {			// Animal is standing
					currentMovement = "standing";	
				} else if (maxAmp < 0.07) {			// Animal is walking
					currentMovement = "walking";		
				} else if (maxAmp >= 0.07) {		// Animal is running
					currentMovement = "running";	
				}
				
				System.out.println("Current:" + currentMovement);			
				
				/* Check if the type of movement changed since last data saving */
				if (!(currentMovement.equals(lastMovement))) {
					
					/* If the type of movement changed, save the new state in the database */
					DatabaseConnection.insertDataMovement(wearableID, currentMovement);
				}
				
				/* Save current movement as last movement, so the next comparison will work */
				lastMovement = currentMovement;
			}
		
		} catch (SocketException e) {
			System.out.println("Disconnected");
			run = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
}
