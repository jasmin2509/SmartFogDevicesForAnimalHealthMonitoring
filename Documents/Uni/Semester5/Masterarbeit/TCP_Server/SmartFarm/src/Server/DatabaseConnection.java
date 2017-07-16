package Server;

import java.sql.*;
import java.util.Date;

/**
 * The class DatabaseConnection is designed to organize the connection and communication
 * to the database
 * 
 * @author Jasmin
 */
public class DatabaseConnection
{
	/* Declare the following variables as static, because they are needed in the createConnection-method
	 * which must be static, because it is used from the WorkStation class
	 */
	private static final String driver = "com.mysql.jdbc.Driver";
	/*
	 * hostname, port, dbname, user and password must be adapted to the new database
	 */
	final static String hostname = "wp168.webpack.hosteurope.de"; 
    final static String port = "3306";
	private static String dbname = "db12431792-smartfarm";
	private static String user = "db12431792-user";
	private static String password = "smartfarm";
 
    private static Connection con;
	
	/**
	 * Creates the connection with the database in accordance to the above-mentioned parameters
	 */
	public static void createConnection()
	{
		try { 
			
				/* Load the mysql jdbc driver */
	      	    Class.forName(driver).newInstance(); 
	        
		} 
		
	    catch (Exception e) { 
	    	System.err.println("Unable to load driver."); 
	    	e.printStackTrace(); 
	    } 
		
		try { 
			
			/* Create connection and save it in variable con */
			String url = "jdbc:mysql://"+hostname+":"+port+"/"+dbname; 
		    con = DriverManager.getConnection(url, user, password); 
		    
	    }
		
		/* Catch Exceptions */
		catch (SQLException sqle) { 
			System.out.println("SQLException: " + sqle.getMessage()); 
			System.out.println("SQLState: " + sqle.getSQLState()); 
			System.out.println("VendorError: " + sqle.getErrorCode()); 
			sqle.printStackTrace(); 
		} 
	}
	
	
	/**
	 * Close the connection to the database
	 */
	public static void closeConnection()
	{
		try 
		{
			con.close();
		}
		catch (SQLException sqle) 
		{
			System.out.println(sqle.toString());
		}
	}
	
	/**
	 * Send query to database and execute it
	 * 
	 * @param query The query that shall be executed
	 * @return rs ResultSet that was given back after executing the query
	 */
	public static ResultSet request(String query) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            return null;
        }
    }

	
	/**
	 * Insert data in the table BodyTemperature
	 * 
	 * @param WearableID The ID of the WearablePi which is the MAC address of the Raspberry Pi
	 * @param BodyTemp The measured body temperature
	 * 
	 * @return boolean which stands for the insertion's success
	 */
	public static boolean insertDataBodyTemperature(String WearableID, double BodyTemp) {
		try {
			java.util.Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
		    String query = "INSERT INTO BodyTemperature (WearableID, BodyTemperature, Time)" + " VALUES (?, ?, ?)";
		    
		    /* Create mysql insert preparedStatement */
		    PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, WearableID);
		    preparedStmt.setDouble (2, BodyTemp);
		    preparedStmt.setTimestamp (3, timestamp);

		    /* Execute preparedStatement */
		    preparedStmt.execute();
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Insert data in the table Movement
	 * 
	 * @param WearableID The ID of the WearablePi which is the MAC address of the Raspberry Pi
	 * @param Movement The current Movement state (String that says which movement state the animal has)
	 * 
	 * @return boolean which stands for the insertion's success
	 */
	public static boolean insertDataMovement(String WearableID, String Movement) { 
		try {
			java.util.Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
		    String query = "INSERT INTO Movement (WearableID, Movement, Time)" + " VALUES (?, ?, ?)";
		    
		    /* Create mySQL insert preparedStatement */
		    PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, WearableID);
		    preparedStmt.setString (2, Movement);								
		    preparedStmt.setTimestamp (3, timestamp);

		    /* Execute preparedStatement */
		    preparedStmt.execute();
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Insert data in the table RoomParameter
	 * 
	 * @param EnvironmentID The ID of the EnvironmentPi which is the MAC address of the Raspberry Pi
	 * @param temp The stable's room temperature
	 * @param humidity The stable's humidity
	 * @param tempAction The current state of the tempAction (heating on (=heat) or air condition on (=airc) or nothing (=null)
	 * 
	 * @return boolean which stands for the insertion's success
	 */
	public static boolean insertDataRoomParameter(String EnvironmentID, float temp, float humidity, String tempAction) {
		try {
			java.util.Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
		    String query = "INSERT INTO RoomParameter (EnvironmentID, Temperature, Humidity, StateTempAction, Time)" + " VALUES (?, ?, ?, ?, ?)";
		    
		    /* Create mysql insert preparedStatement */
		    PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.setString (1, EnvironmentID);
		    preparedStmt.setFloat (2, temp);
		    preparedStmt.setFloat (3, humidity);
		    preparedStmt.setString (4, tempAction);
		    preparedStmt.setTimestamp (5, timestamp);

		    /* Execute preparedStatement */
		    preparedStmt.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Get the last saved type of movement of a certain animal by ordering the data by time (descending) and take the first value
	 * 
	 * @param wearableID The ID of the animal's device which last movement data shall be get
	 * @return lastMovement The String that describes the type of movement which was last saved in the database
	 */
	public static String getLastMovement (String wearableID) {
		String query = "SELECT * FROM `Movement` WHERE WearableID = '" + wearableID + "' ORDER BY Time DESC";
		String lastMovement = null;
		ResultSet result = request(query);
		try {
			if (!result.next()) return lastMovement;
			else {
				result.next();
				lastMovement = result.getString("Movement");
				int id = result.getInt("ID");
				System.out.println("Zugehörige ID: " + id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return lastMovement;
	}
	
	
	/**
	 * main-method, just for testing purposes
	 */
	public static void main(String args[]) throws Exception {
		createConnection();
	}
	
	
	/**
	 * Checks if an animal is already registered in the database
	 * 
	 * @param wearableID The ID of the animal's device which registration shall be checked
	 * 
	 * @return boolean Determines if the animal is already registered or not
	 */
	public static boolean isAnimalRegistered (String wearableID) {
		String query = "SELECT AnimalID FROM AnimalRegistry WHERE WearableID = '" + wearableID + "'";
		ResultSet result = request(query);
		try {
			if (!result.next()) return false;
			else return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException ne) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Registers an animal with the matching wearableID in the database 
	 * 
	 * @param wearableID The ID of the animal's device which shall be registered
	 * @param animalIDstring The ID of the animal 
	 * 
	 * @return boolean Determines if the registration was successful
	 */
	public static boolean registerAnimal(String wearableID, String animalIDstring) {
		int animalID;
		
		/* Intercept wrong inputs */
		if (animalIDstring == null || animalIDstring == "") {
			return false;
		} else {
			animalID = Integer.parseInt(animalIDstring);
			String query = "INSERT INTO AnimalRegistry (WearableID, AnimalID)" + " VALUES (?, ?)";
		    try {
		    	
		    	/* Create mysql insert preparedStatement */
		    	PreparedStatement preparedStmt = con.prepareStatement(query);
				preparedStmt.setString (1, wearableID);
				preparedStmt.setInt (2, animalID);
				System.out.println("Register Animal");
			    
				/* Execute preparedStatement */
				preparedStmt.execute();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    return true;
		}
		    
	}
	
	
	/**
	 * Delete the registration of a certain animal
	 * 
	 * @param animalID ID of the animal which registration shall be deleted
	 */
	public static void deregisterAnimal(String animalID) {
		String query = "DELETE FROM AnimalRegistry WHERE AnimalID = ?";
	    try {
	    	
	    	/* Create mysql insert preparedStatement */
	    	PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, animalID);
			
			/* Execute preparedStatement */
		    preparedStmt.execute();
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the matching animalID (number of the animal) to a certain wearableID (MAC address of a Raspi) 
	 *
	 * @param wearableID The devices' ID to which the matching animalID shall be found
	 * 
	 * @return animalID The questioned animalID, if not found 0
	 */
	public static int getAnimalID(String wearableID){
		String query = "SELECT AnimalID FROM AnimalRegistry WHERE WearableID = '" + wearableID + "'";
		ResultSet result = request(query);
		try {
			result.next();
			String animalIDString = result.getString("AnimalID");
			int animalID = Integer.parseInt(animalIDString);
			return animalID;			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;	// TODO: Eingabe vom Farmer fuer animalID darf nicht 0 sein!
		}
	}
	
	
	/**
	 * Get the matching wearableID (MAC address of a Raspi) to a certain animalID (number of the animal)
	 *
	 * @param animalID The animal's ID to which the matching wearableID shall be found
	 * 
	 * @return wearableID The questioned wearableID, if not found 0
	 */
	public static String getWearableID(int animalID) {
		String query = "SELECT WearableID FROM AnimalRegistry WHERE AnimalID = '" + animalID + "'";
		ResultSet result = request(query);
		try {
			result.next();
			String wearableID= result.getString("WearableID");
			return wearableID;			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;	// ACHTUNG: Eingabe vom Farmer fuer stableID darf nicht 0 sein!
		}
	}
	
	
	/**
	 * Checks if an stable is already registered in the database
	 * 
	 * @param environmentID The ID of the stable's device which registration shall be checked
	 * 
	 * @return boolean Determines if the stable is already registered or not
	 */
	public static boolean isStableRegistered (String environmentID) {
		String query = "SELECT StableID FROM StableRegistry WHERE EnvironmentID = '" + environmentID + "'";
		ResultSet result = request(query);
		try {
			if (!result.next()) return false;
			else return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException ne) {
			return false;
		}

		return true;
	}
	
	
	/**
	 * Registers a stable with the matching environmentID in the database 
	 * 
	 * @param environmentID The ID of the stable's device which shall be registered
	 * @param stableIDstring The ID of the stable
	 * 
	 * @return boolean Determines if registration was successful or not
	 */
	public static boolean registerStable(String environmentID, String stableIDstring) {
		int stableID;
		if (stableIDstring == null || stableIDstring == "") {
			return false;
		} else {
			stableID = Integer.parseInt(stableIDstring);
		
			String query = "INSERT INTO StableRegistry (EnvironmentID, StableID)" + " VALUES (?, ?)";
		    try {
		    	
		    	PreparedStatement preparedStmt = con.prepareStatement(query);
				preparedStmt.setString (1, environmentID);
				preparedStmt.setInt (2, stableID);
			    preparedStmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    return true;
		}
	}
	
	
	/**
	 * Delete the registration of a certain stable
	 * 
	 * @param stableID ID of the stable which registration shall be deleted
	 */
	public static void deregisterStable(String stableID) {
		String query = "DELETE FROM StableRegistry WHERE StableID = ?";
	    try {
	    	PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.setString (1, stableID);
		    preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Get the matching stableID (number of the stable) to a certain environmentID (MAC address of a Raspi) 
	 *
	 * @param environmentID The devices' ID to which the matching stableID shall be found
	 * 
	 * @return stableID The questioned stableID, if not found 0
	 */
	public static int getStableID(String environmentID){
		String query = "SELECT StableID FROM StableRegistry WHERE EnvironmentID = '" + environmentID + "'";
		ResultSet result = request(query);
		try {
			result.next();
			String stableIDString = result.getString("StableID");
			int stableID = Integer.parseInt(stableIDString);
			return stableID;			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;	// ACHTUNG: Eingabe vom Farmer fuer stableID darf nicht 0 sein!
		}
	}
	
	
	/**
	 * Get the matching environmentID (MAC address of a Raspi) to a certain stableID (number of the stable)  
	 *
	 * @param stableID The stable's ID to which the matching environmentID shall be found
	 * 
	 * @return environmentID The questioned environmentID, if not found 0
	 */
	public static String getEnvironmentID(int stableID) {
		String query = "SELECT EnvironmentID FROM StableRegistry WHERE StableID = '" + stableID + "'";
		ResultSet result = request(query);
		try {
			result.next();
			String environmentID = result.getString("EnvironmentID");
			return environmentID;			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;	// ACHTUNG: Eingabe vom Farmer fuer stableID darf nicht 0 sein!
		}
	}
	
	
	/**
	 * Get the whole stable registry as a resultSet
	 * 
	 * @return result ResultSet which includes all registry entries of the stable registry
	 */
	public static ResultSet getStableRegistry() {
		String query = "SELECT * FROM StableRegistry";
		ResultSet result = request(query);
		return result;
	}
	
	
	/**
	 * Get the whole animal registry as a resultSet
	 * 
	 * @return result ResultSet which includes all registry entries of the animal registry
	 */
	public static ResultSet getAnimalRegistry() {
		String query = "SELECT * FROM AnimalRegistry";
		ResultSet result = request(query);
		return result;
	}
	
	
}