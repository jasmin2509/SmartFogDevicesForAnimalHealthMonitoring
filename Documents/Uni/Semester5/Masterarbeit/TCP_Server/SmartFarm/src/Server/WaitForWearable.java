package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * The class WaitForWearable implements the class Runnable so that it creates a new Thread by creating an object
 * It is designed for listening on a certain port for connecting Wearble R-Pi's
 */
public class WaitForWearable implements Runnable{
	
	ServerSocket serverForWearables;
	
	/**
	 * Constructor
	 * @param serverForWearables the Socket for the connection to Wearable R-Pi's on a certain port
	 */
	public WaitForWearable(ServerSocket serverForWearables) {
		this.serverForWearables = serverForWearables;
	}

	
	/**
	 * The run-method of the thread/object
	 */
	public void run() {
		
		/* 
		 * Variable to stop the while-loop, at the moment not needed, because there is no need to stop 
		 * the object listening the port while the system is running
		 */
		boolean run = true;
		
		/* Current an endless loop */
		while (run) {
			
			Socket wearableSocket;
			
			try {
			
				/* Accept the connection request of an Environment R-Pi, if received */
				wearableSocket = serverForWearables.accept();
				
				/* Start new Thread for new Environment R-Pi */
				Thread t = new Thread(new WearablePi(wearableSocket));
				t.start();
				
				// System.out.println("New Wearable Thread started");
			
			/* Catch IOException */
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
		}
	}	
}
