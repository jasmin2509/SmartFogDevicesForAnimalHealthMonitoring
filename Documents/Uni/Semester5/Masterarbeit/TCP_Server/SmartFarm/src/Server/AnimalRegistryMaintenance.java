package Server;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * The class AnimalRegistryMaintenance is designed to open a window in which
 * the user can maintain the animal registry, which means change an animal number
 * or deregister a device
 * 
 * @author Jasmin
 */
public class AnimalRegistryMaintenance extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	static JComboBox<String> comboBoxAnimal;
	JButton btnDeregister;
	JButton btnEdit;
	static JPanel panel;
	static GridBagConstraints gbc = new GridBagConstraints();
	static GridBagLayout layout = new GridBagLayout();
	static JLabel deleteSuccessful = new JLabel("The deregistration was successful.");
	static JLabel changeSuccessful = new JLabel("Edit in registry was successful.");
	static JLabel empty = new JLabel("No entry in animal registry");

	/**
	 * Create the application.
	 */
	public AnimalRegistryMaintenance() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame
	 */
	private void initialize() {
		
		setTitle("Animal Registry Maintenance"); 
		setBounds(100, 100, 500, 160);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		gbc.insets = new Insets(5,15,5,15); //oben, links, unten, rechtsframe = new JFrame();
		
		panel = new JPanel();
		panel.setLayout(layout);
		panel.setBackground(new Color(157, 253, 171));
		JLabel heading = new JLabel("Maintenance of the animal registry"); // Heading
		heading.setFont(new Font("Tahoma", Font.BOLD, 14));
		gbc.fill = GridBagConstraints.BOTH;
		addComponent(panel, layout, heading, 0, 0, 3, 1, 1.0, 0, gbc);
		
		comboBoxAnimal = new JComboBox<String>(); // ComboBox for choosing stable number
		comboBoxAnimal.setToolTipText("Choose Animal Number");
		addComponent(panel, layout, comboBoxAnimal, 0, 1, 1, 1, 1.0, 0, gbc);
		
		btnDeregister = new JButton("Deregister device"); // Button for the maintenance of the stable registry
		btnDeregister.addActionListener(this);
		addComponent(panel, layout, btnDeregister, 1, 1, 1, 1, 0, 0, gbc);
		
		btnEdit = new JButton("Edit Animal Number"); // Button for the maintenance of the stable registry
		btnEdit.addActionListener(this);
		addComponent(panel, layout, btnEdit, 2, 1, 1, 1, 0, 0, gbc);
		
		setComboBoxItems();
		
		addComponent(panel, layout, empty, 0, 2, 3, 1, 0, 0, gbc);
		addComponent(panel, layout, changeSuccessful, 0, 2, 3, 1, 0, 0, gbc);
		addComponent(panel, layout, deleteSuccessful, 0, 2, 3, 1, 0, 0, gbc);
		clearInformation();
		
		add(panel);
		
	}

	
	/**
	 * The action listener of the buttons of the window
	 * 
	 * @param ae ActionEvent that shows which element is responsible for the action
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		/* Check if the source of the action event is the "deregister" button */
		 if (ae.getSource() == this.btnDeregister){
			 
			 /* If the user wants to deregister a devive, get the selected item in the comboBox */
			 String animalIDstring = (String)comboBoxAnimal.getSelectedItem();
			 
			 /* Then delete the registration entry in the animal registry in the database */
	   		 DatabaseConnection.deregisterAnimal(animalIDstring);
	   		 
	   		 /* Update the comboBox elements and show the user that the action was successful */
	   		 setComboBoxItems();
	   		 GUIWorkStation.setComboBoxAnimalItems();
	   		 clearInformation();
	   		 deleteSuccessful.setVisible(true);
	   		 
	   	 /* Check if the source of the action is the "edit" button */
		 } else if (ae.getSource() == this.btnEdit) {
			 
			 /* If the user wants to edit an animal registration, get the selected item in the comboBox*/
			 String oldAnimalIDstring = (String)comboBoxAnimal.getSelectedItem();
			 
			 /* Check if there is a current element in the comboBox and if it is valid */
			 if (oldAnimalIDstring != null && oldAnimalIDstring != ""){
				 
				 /* Open an AnimalRegistrationWindow to enable the input of a new AnimalID */
				 String newAnimalID = AnimalRegistrationWindow.display();
				 
				 /* Check if the new AnimalID is valid*/
				 if (newAnimalID != null && newAnimalID != "") {
					 
					 /* Delete the old registry entry and add the new one, then update the window's information */
					 int oldAnimalID = Integer.parseInt(oldAnimalIDstring);
					 String animalID = DatabaseConnection.getWearableID(oldAnimalID);
					 DatabaseConnection.deregisterAnimal(oldAnimalIDstring);
					 DatabaseConnection.registerAnimal(animalID, newAnimalID);
					 setComboBoxItems();
			   		 GUIWorkStation.setComboBoxAnimalItems();
			   		 clearInformation();
			   		 changeSuccessful.setVisible(true);
				 }
			 }
		 }
	}
	
	
	/**
	 * This method is designed to update the comboBox elements
	 */
	static void setComboBoxItems() {
		
		/* Delete all elements and get the complete animal registry from the database */
		comboBoxAnimal.removeAllItems();
		ResultSet animalRegistry = DatabaseConnection.getAnimalRegistry();
		boolean records = false;
		try {
			/* Add for every registry entry a comboBox element */
			while(animalRegistry.next()) {
				records = true;
				String animalID = animalRegistry.getString("AnimalID");
				comboBoxAnimal.addItem(animalID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/* If there are no entries in the registry, show an empty comboBox */
		if (!records) {
			clearInformation();
			empty.setVisible(true);
		}
	}
	

	/**
	 * The method addComponent is designed to add a certain component (like label, button etc.) to a certain JPanel
	 * It needs a lot of arguments but it is much less elaborate than specifying all parameters individually
	 * Especially because it is needed a lot of times
	 * 
	 * @param cont The JPanel wo which the component should be added
	 * @param gbl The GridBagLayout that should be used
	 * @param c The Component that should be added
	 * @param x The value for the GridBagConstraint that should be used to the x-axis
	 * @param y The value for the GridBagConstraint that should be used to the x-axis
	 * @param width The width that the component should have (measured in columns)
	 * @param height The height that the component should have (measured in lines)
	 * @param weightx Distributes extra space in x-axis 
	 * @param weighty Distributes extra space in y-axis
	 * @param gbc The GridBagConstraints that should be added to the GridBagLayout
	 */
	static void addComponent( JPanel cont, GridBagLayout gbl, Component c, int x, int y, int width, int height, double weightx, double weighty, GridBagConstraints gbc) {
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x; 
		gbc.gridy = y;
		gbc.gridwidth = width; 
		gbc.gridheight = height;
		gbc.weightx = weightx; 
		gbc.weighty = weighty;
		gbl.setConstraints( c, gbc );
		cont.add( c );
	}
	
	
	/**
	 *  Make all the possible shown informations invisible 
	 */
	static void clearInformation() {
		changeSuccessful.setVisible(false);
		deleteSuccessful.setVisible(false);
		empty.setVisible(false);
	}

}
