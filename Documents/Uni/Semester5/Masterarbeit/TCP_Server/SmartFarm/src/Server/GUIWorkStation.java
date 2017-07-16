package Server;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * The class GUIWorkStation creates the system's Graphical User Interface
 * The user can see different parameters of the stable and the animals in it
 * and can manage the system
 * @author Jasmin
 */
public class GUIWorkStation extends JFrame implements ActionListener {
	
	/**
	 * Define some paramters static:
	 * All labels that shall show current information about the stable or the animal
	 * The ComboBoxes that shall include all devices
	 * Because all these elements must be accessed by other classes to change them
	 */
	private static final long serialVersionUID = 1L;
	public static JPanel panelInfo;
	public static JComboBox<String> comboBoxStableNumber;
	public static JComboBox<String> comboBoxAnimal;
	public static JLabel lblBodyTempValue;
	public static JLabel lblTempValue;
	public static JLabel lblHumidityValue;
	JButton btnCamera;
	JButton btnMaintainStableRegistry;
	JButton btnMaintainAnimalRegistry;
	JButton btnChangeInit;
	Dimension SizeCombobox = new Dimension(116, 20);
	GridBagLayout layoutPanelInfo = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	Color green = new Color(0, 255, 102);
	Color lightgreen = new Color(157, 253, 171);
	
	
	/**
	 * Main method, just for testing purposes
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUIWorkStation window = new GUIWorkStation();
//					window.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Constructor, creates the GUI
	 */
	public GUIWorkStation() {
		initialize();
	}

	/**
	 * Initialize the GUI's contents
	 */
	private void initialize() {
		
		/* Set size, background color, layout and insets */
		getContentPane().setBackground(lightgreen);
		setTitle("SmartFarm Application"); 
		setBounds(100, 100, 750, 550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout layoutFrame = new GridBagLayout();
		setLayout(layoutFrame);
		gbc.insets = new Insets(10,20,10,20); //oben, links, unten, rechts
		
		/* Add a panel and make the background invisible */
		JPanel panelTitle = new JPanel(); 
		panelTitle.setBackground(new Color(0, 0, 0, 0));
		
		/* Add the heading */
		JLabel lblSmartfarmApplication = new JLabel("SmartFarm Application");
		lblSmartfarmApplication.setFont(new Font("Tahoma", Font.BOLD, 20));
		addComponent(panelTitle, layoutFrame, lblSmartfarmApplication, 0, 0, 1, 1, 0, 0, gbc);
		
		/* Add a panel to show in it the stable's information */
		panelInfo = new JPanel();
		panelInfo.setBackground(lightgreen);
		panelInfo.setLayout(layoutPanelInfo);
		
		/* Heading for the stable's information part */
		JLabel lblStable = new JLabel("Stables");
		lblStable.setFont(new Font("Tahoma", Font.BOLD, 18));
		addComponent(panelInfo, layoutPanelInfo, lblStable, 0, 0, 4, 1, 0, 0, gbc);
		
		/* Description of ComboBox for choosing stable number */
		JLabel lblStableNumber = new JLabel("Choose Stable Number:");
		addComponent(panelInfo, layoutPanelInfo, lblStableNumber, 0, 1, 1, 1, 0, 0, gbc);
		
		/* Label for Temperature description */
		JLabel lblTemperature = new JLabel("Temperature");
		addComponent(panelInfo, layoutPanelInfo, lblTemperature, 1, 1, 1, 1, 0, 0, gbc);

		/* Label for Humidity description */
		JLabel lblHumidity = new JLabel("Humidity");
		addComponent(panelInfo, layoutPanelInfo, lblHumidity, 2, 1, 1, 1, 0, 0, gbc);
		
		/* ComboBox for choosing stable number */
		comboBoxStableNumber = new JComboBox<String>();
		comboBoxStableNumber.setToolTipText("Choose Stable Number");
		comboBoxStableNumber.setPreferredSize(SizeCombobox);
		comboBoxStableNumber.addActionListener(this);
		addComponent(panelInfo, layoutPanelInfo, comboBoxStableNumber, 0, 2, 1, 1, 0, 0, gbc);
		
		/* Label in which the current temperature will be presented */
	    lblTempValue = new JLabel("");
		lblTempValue.setForeground(Color.BLACK);
		lblTempValue.setBackground(lightgreen);
		addComponent(panelInfo, layoutPanelInfo, lblTempValue, 1, 2, 1, 1, 1.0, 0, gbc);
		
		/* Label in which the current humidity will be presented */
	    lblHumidityValue = new JLabel(""); 
		lblHumidityValue.setForeground(Color.BLACK);
		lblHumidityValue.setBackground(lightgreen);
		lblHumidityValue.setPreferredSize(new Dimension(100, 20)); 					// Solves display problem
		addComponent(panelInfo, layoutPanelInfo, lblHumidityValue, 2, 2, 1, 1, 1.0, 0, gbc);
	    
		/* Button for requesting a live stream from current chosen stable */
		btnCamera = new JButton("Camera Live View");
		btnCamera.addActionListener(this);
		btnCamera.setToolTipText("Opens a window with a camera live view from the chosen stable");
		addComponent(panelInfo, layoutPanelInfo, btnCamera, 0, 3, 1, 1, 0, 0, gbc);
		
		/* Button that opens a window in which the stable registry can be maintained */
		btnMaintainStableRegistry = new JButton("Maintain Stable Registry"); 
		btnMaintainStableRegistry.addActionListener(this);
		addComponent(panelInfo, layoutPanelInfo, btnMaintainStableRegistry, 3, 3, 1, 1, 0, 0, gbc);
		
		/* Line for seperating the stable and the animal information */
		JSeparator line = new JSeparator();
		line.setPreferredSize(new Dimension(120,5));
		addComponent(panelInfo, layoutPanelInfo, line, 0, 4, 4, 1, 0, 0, gbc);		
		
		/* Heading for the animals's information part */
		JLabel lblAnimals = new JLabel("Animals");
		lblAnimals.setFont(new Font("Tahoma", Font.BOLD, 18));
		addComponent(panelInfo, layoutPanelInfo, lblAnimals, 0, 5, 4, 1, 0, 0, gbc);

		/* Description of ComboBox for choosing animal number */
		JLabel lblChooseAnimal = new JLabel("Choose Animal:");
		addComponent(panelInfo, layoutPanelInfo, lblChooseAnimal, 0, 6, 1, 1, 0, 0, gbc);
		
		/* Label for Body Temperature description */
		JLabel lblBodyTemperature = new JLabel("Body Temperature");
		addComponent(panelInfo, layoutPanelInfo, lblBodyTemperature, 1, 6, 1, 1, 0, 0, gbc);

		/* ComboBox for choosing animal number */
		comboBoxAnimal = new JComboBox<String>();
		comboBoxAnimal.setToolTipText("Choose Animal Number");
		comboBoxAnimal.setPreferredSize(SizeCombobox);
		comboBoxAnimal.addActionListener(this);
		addComponent(panelInfo, layoutPanelInfo, comboBoxAnimal, 0, 7, 1, 1, 0, 0, gbc);
		
		/* Label in which the current body temperature will be presented */
		lblBodyTempValue = new JLabel(""); 
		lblBodyTempValue.setForeground(Color.BLACK);
		lblBodyTempValue.setBackground(lightgreen);
		addComponent(panelInfo, layoutPanelInfo, lblBodyTempValue, 1, 7, 1, 1, 0, 0, gbc);
		
		/* Button that opens a window in which the stable registry can be maintained */
		btnMaintainAnimalRegistry = new JButton("Maintain Animal Registry"); // Button for the maintenance of the stable registry
		btnMaintainAnimalRegistry.addActionListener(this);
		addComponent(panelInfo, layoutPanelInfo, btnMaintainAnimalRegistry, 3, 8, 1, 1, 0, 0, gbc);
		
		/* Border arround the information panel */
		panelInfo.setBorder(BorderFactory.createRaisedBevelBorder());
		
		/* Fill GridBagConstraints for layout purposes */
		gbc.fill = GridBagConstraints.BOTH;
		
		/* Add panels to frame */
		addComponentToFrame(this, layoutFrame, panelTitle, 0, 0, 1, 1, 0, 0);
		addComponentToFrame(this, layoutFrame, panelInfo, 0, 1, 1, 6, 0, 0);	

		/* Fill GridBagConstraints for layout purposes */
		gbc.fill = GridBagConstraints.VERTICAL;
		
		/* Button that opens a window in which the initilization values can be changed by the user */
		btnChangeInit = new JButton("Edit initialization values");
		btnChangeInit.addActionListener(this);
		addComponentToFrame(this, layoutFrame, btnChangeInit, 0, 8, 1, 1, 1.0, 0);	
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
	 * The method addComponentToFrame is designed to add a certain component (like panel, label, button etc.) to the JFrame
	 * It needs a lot of arguments but it is much less elaborate than specifying all parameters individually
	 * 
	 * @param cont The GUIWorkStation object to which the component should be added
	 * @param gbc The GridBagConstraints that should be added to the GridBagLayout
	 * @param c The Component that should be added
	 * @param x The value for the GridBagConstraint that should be used to the x-axis
	 * @param y The value for the GridBagConstraint that should be used to the x-axis
	 * @param width The width that the component should have (measured in columns)
	 * @param height The height that the component should have (measured in lines)
	 * @param weightx Distributes extra space in x-axis 
	 * @param weighty Distributes extra space in y-axis
	 */
	static void addComponentToFrame( GUIWorkStation cont, GridBagLayout gbl, Component c, int x, int y, int width, int height, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(10,10,10,10);
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
	 * Updates the shown stable values in the GUI  
	 * Temperature is shown red, if it is over the specified maximum temperature
	 * and blue if it is below the specified minimum temperature
	 * 
	 * @param tempstr Current temperature in the stable as a String
	 * @param humidity Current humidity in the stable as a String
	 * @param minTemp Chosen minimum temperature	
	 * @param maxTemp Chosen maximum temperature
	 */
    public void showValuesStable (String tempstr, String humidity, float minTemp, float maxTemp){
    	float temp = Float.parseFloat(tempstr);
    	lblTempValue.setOpaque(true);
    	if (temp > maxTemp) {
    		/* Turn text red if temperature is too high */
    		lblTempValue.setText("<html><p><font color=red size=+2>" + tempstr + " °C</font><p></html>");
    	}
    	else if (temp < minTemp) {
    		/* Turn text blue if temperature is too low */
    		lblTempValue.setText("<html><p><font color=blue size=+2>" + tempstr + " °C</font><p></html>");
    	}
    	else {
    		lblTempValue.setText("<html><p><font size=+2>" + tempstr + " °C</font><p></html>");       	
    	}

    	lblHumidityValue.setOpaque(true);
    	lblHumidityValue.setText("<html><p><font size=+2>" + humidity + " %</font><p></html>");
    }
    
    /**
	 * Updates the shown animal values in the GUI  
	 * 
	 * @param bodyTemp current body temperature of the chosen animal as a String
	 */
    public void showValuesAnimal (String bodytemp){
    	lblBodyTempValue.setOpaque(true);
    	lblBodyTempValue.setText("<html><p><font size=+2>" + bodytemp + " °C</font><p></html>");
    }
    

    /**
     * This method overrides the actionPerformed-method, that is part ot the ActionListener-class
     * Is is designed to act on actions performed in the GUI by the user
     * 
     * @param ae The ActionEvent that is occured
     */
    @Override
    public void actionPerformed (ActionEvent ae){
    	
    	/* The user pressed the Camera Live Feed button */
        if (ae.getSource() == this.btnCamera){
        	
        	/* Determine from which Environment R-Pi the camera live view shall be */
        	String stableIDstring = (String)comboBoxStableNumber.getSelectedItem();
   		 	if (stableIDstring != null && stableIDstring != "") {
   		 		int stableID = Integer.parseInt(stableIDstring);
   		 		String environmentID = DatabaseConnection.getEnvironmentID(stableID);
   		 		EnvironmentPi environObject = EnvironmentPi.getEnvironmentPi(environmentID);
   		 		
   		 		/* Send a message to the Environment R-Pi for requesting the camera live view 
   		 		 * At this time there is no reaction from the Environment R-Pi because this 
   		 		 * functionality is not implemented yet
   		 		 */
   		 		ClientResponder.sendToClient("cam", environObject.environmentCameraRequestStream);
   		 	}
   		 	
   		/* The user pressed the button Maintain Stable Registry */
        } else if (ae.getSource() == this.btnMaintainStableRegistry) {
        	
        	/* Open Stable Registry window and make it visible */
        	StableRegistryMaintenance window = new StableRegistryMaintenance();
			window.setVisible(true);
        
		/* The user pressed the button Maintain Animal Registry */
        } else if (ae.getSource() == this.btnMaintainAnimalRegistry) {

        	/* Open Animal Registry window and make it visible */
        	AnimalRegistryMaintenance window = new AnimalRegistryMaintenance();
			window.setVisible(true);
			
		/* The user changed the value of the Animal ComboBox 
		 * Here is nothing to do, because the threads that communicate with the
		 * Raspberry Pi's continuously check if their animal number is selected
		 */	
        } else if (ae.getSource() == comboBoxAnimal) {
        	getContentPane().setBackground(new Color(157, 253, 171));

        /* The user changed the value of the Stable ComboBox 
    	 * Here is nothing to do, because the threads that communicate with the
    	 * Raspberry Pi's continuously check if their stable number is selected
    	 */	
        } else if (ae.getSource() == comboBoxStableNumber) {
        	getContentPane().setBackground(new Color(157, 253, 171));
        	
        /* The user pressed the Button Change Initialization values */	
        } else if (ae.getSource() == btnChangeInit) {
        	/* Open Initialization Window again */
        	InitializationWindow init = new InitializationWindow();
        }
    }
	
    
    /**
     * This method is designed to get all entries out of the animal registry
     * that is stored in the database and create ComboBoxElements for each
     * This method is used, if something in the registry changed
     */
	static void setComboBoxAnimalItems() {
		
		/* Clear the ComboBox */
		comboBoxAnimal.removeAllItems();
		
		/* Get all entries from the database within the AnimalRegistry */
		ResultSet animalRegistry = DatabaseConnection.getAnimalRegistry();
		try {
			while(animalRegistry.next()) {
				
				/* As long there is a next element enter it in the ComboBox */
				String animalID = animalRegistry.getString("AnimalID");
				comboBoxAnimal.addItem(animalID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * This method is designed to get all entries out of the stable registry
     * that is stored in the database and create ComboBoxElements for each
     * This method is used, if something in the registry changed
     */
	static void setComboBoxStableItems() {
		
		/* Clear the ComboBox */
		comboBoxStableNumber.removeAllItems();
		
		/* Get all entries from the database within the StableRegistry */
		ResultSet stableRegistry = DatabaseConnection.getStableRegistry();
		try {
			while(stableRegistry.next()) {
				
				/* As long there is a next element enter it in the ComboBox */
				String stableID = stableRegistry.getString("StableID");
				comboBoxStableNumber.addItem(stableID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
}

