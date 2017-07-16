package Server;

import java.io.*;
import java.util.StringTokenizer;

class ClientResponder {
	
	/**
	 * Send as message in form of a String to a specific client identified by a PrintWriter
	 * 
	 * @param response The message that shall be sent to the client
	 * @param ouToClient The PrintWriter that enables the communication to a specific client
	 */
    public static void sendToClient(String response, PrintWriter outToClient) {
    	outToClient.print(response);
		outToClient.flush();
    }
	
	/**
	 * Divide String at spaces into multiple Strings and save them in an String Array
	 * 
	 * @param receivedString The String that was received from the Client and shall be divided
	 * 
	 * @return arrayFromClient Array of Strings that include all parts of the original String
	 */
    public static String [] resolveStringToArray(String receivedString) {
    	StringTokenizer token;
    	token = new StringTokenizer(receivedString);
    	int length = token.countTokens();  // number of parts found
    	String[] arrayFromClient = new String[ length ];  // create array
    	for( int i = 0; i < length; i++ )
    		arrayFromClient[i] = token.nextToken();  // save several parts
    	return arrayFromClient;
    }
    
 }