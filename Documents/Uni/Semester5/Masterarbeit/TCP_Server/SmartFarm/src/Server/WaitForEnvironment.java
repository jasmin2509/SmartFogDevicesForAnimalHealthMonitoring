package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * The class WaitForEnvironment implements the class Runnable so that it creates a new Thread by creating an object
 * It is designed for listening on a certain port for connecting Environment R-Pi's
 */
public class WaitForEnvironment implements Runnable{
	
	ServerSocket serverForEnvironment;
	
	/**
	 * Constructor
	 * @param serverForEnvironment the Socket for the connection to Environment R-Pi's on a certain port
	 */
	public WaitForEnvironment(ServerSocket serverForEnvironment) {
		this.serverForEnvironment = serverForEnvironment;
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
			
			Socket environmentSocket;
			
			try {
				
				/* Accept the connection request of an Environment R-Pi, if received */
				environmentSocket = serverForEnvironment.accept();
				
				/* Start new Thread for new Environment R-Pi */
				Thread t = new Thread(new EnvironmentPi(environmentSocket));
				t.start();
				
				// System.out.println("New Environment Thread started");
			
			/* Catch IOException */
			} catch (IOException e) {
				e.printStackTrace();
			}
        	

		}
	}	
}
