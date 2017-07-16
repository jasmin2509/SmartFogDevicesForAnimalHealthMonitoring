package Server;

import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.text.*;
	

/**
 * The class StableRegistrationWindow is designed to open a window for a stable's number input
 * after an Environment-Pi tries to register with the system
 * 
 * @author Jasmin
 */
public class StableRegistrationWindow {
	
	/**
	 * Responsible for showing the window where the input should be made
	 * 
	 * @return StableIDString The number the user wants to match with the device
	 */
	static String display() {
		int insertResult = 0;
		int yesNoResult = 0;
		String stableIDString = null;
		do {
			
		    /* Create text field that allows only to insert numbers and no other characters */
	    	JTextField textField=new JTextField();
	    	textField.setDocument(new PlainDocument()
	    	{
	    		/**
	    		 * A method that allows no other inputs but numbers
	    		 */
				public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		    		String currentText = getText(0, getLength());
		    		String beforeOffset = currentText.substring(0, offs);
		    		String afterOffset = currentText.substring(offs, currentText.length());
		    		String proposedResult = beforeOffset + str + afterOffset;
		    		for(int i=0;i<proposedResult.length();i++) {
		    			if(!Character.isDigit(proposedResult.charAt(i))) {
		    				return;
		    			}            
		    		}
		    		super.insertString(offs, str, a);
		    	}
		    });
	    	
		    JPanel panel = new JPanel(new GridLayout(0, 1));
		    panel.add(new JLabel("Insert Stable Identification Number (only Numbers accepted):"));
		    panel.add(textField);
		    insertResult = JOptionPane.showConfirmDialog(null, panel, "Registration of Environment Monitoring Device", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		    
		    /* Get the input out of the text field */
		    stableIDString = textField.getText();
		    
		    /* Check if the user clicked "OK", if yes, return the entered stableID */
			if (insertResult == JOptionPane.OK_OPTION) {
			    return stableIDString;
			} 
			
			/* If the user did not click "OK", check if he clicked "CANCEL" or closed the window with the "X" */
			else if (insertResult == JOptionPane.CANCEL_OPTION || insertResult == JOptionPane.CLOSED_OPTION){
				
				/* Ask the user if he wants to continue without assigning an ID, if he clicks "YES" return null */
				yesNoResult = JOptionPane.showConfirmDialog(null, "Do your really want to continue without assigning a stable ID? "
						+ "\nYou will need to restart the Environment Monitoring Device "
				 		+ "if you want to do it later!", "Attention", JOptionPane.YES_NO_OPTION);
				 if(yesNoResult == JOptionPane.YES_OPTION){
					 return null;
				 }
			}
			
		/* Repeat the loop as long as the user not clicks "OK" and non confirms the dialogue with "YES" */
		} while(insertResult != JOptionPane.OK_OPTION && yesNoResult != JOptionPane.YES_OPTION);
		return stableIDString;
		
	}

}