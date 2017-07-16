package Server;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


/**
 * The class InitializationWindow is designed to open a window, where the user must enter (or confirm the predefined) values
 * that the system need to work, like minimum and maximum stable temperature, tolerance and the maximum body temperature
 * 
 * @author Jasmin
 */
public class InitializationWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	JButton btnSave;
	static JPanel panel;
	static GridBagConstraints gbc = new GridBagConstraints();
	static GridBagLayout layout = new GridBagLayout();
	SpinnerNumberModel spinnerModel;
	JSpinner spinnerMinTemp;
	JSpinner spinnerMaxTemp;
	JSpinner toleranceSpinner;
	JSpinner bodyTempSpinner;
	JSpinner.NumberEditor editor;
	JPanel panelError1;
	JPanel panelError2;

	/**
	 * Constructor
	 */
	public InitializationWindow() {
		display();
	}
	
	
	/**
	 * Method that is responsible to create and show the window where the initialization values shall be entered
	 */
	void display() {
		
		/* Title and general settings */
		setTitle("System Initialization"); 
		setBounds(220, 150, 500, 500);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		/* Variables for the necessary values */
		int minTemp = (int) EnvironmentPi.minTemp;
		int maxTemp = (int) EnvironmentPi.maxTemp;
		double tolerance = EnvironmentPi.tolerance;
		double maxBodyTemp = WearablePi.maxBodyTemp;
		
		/* Panel and heading settings */
		gbc.insets = new Insets(5,15,5,15); 
		panel = new JPanel();
		panel.setLayout(layout);
		panel.setBackground(new Color(157, 253, 171));
		JLabel heading = new JLabel("System Initialization Window"); // Heading
		heading.setFont(new Font("Tahoma", Font.BOLD, 14));
		addComponent(panel, layout, heading, 0, 0, 1, 1, 1.0, 0, gbc);
		
		
		/* Minimum temperature */
		JLabel minTempLabel = new JLabel("Choose the minimal temperature of the stable"); // MinTemp
		minTempLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addComponent(panel, layout, minTempLabel, 0, 1, 1, 1, 0, 0, gbc);

		JLabel minTempDesc = new JLabel("(The heating will be turned on if the temperature is below this limit)");
		minTempDesc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addComponent(panel, layout, minTempDesc, 0, 2, 1, 1, 0, 0, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		spinnerMinTemp = new JSpinner();
		if(minTemp == 0) spinnerMinTemp.setModel(new SpinnerNumberModel(19, 10, 30, 1));
		else spinnerMinTemp.setModel(new SpinnerNumberModel(minTemp, 10, 30, 1));
		addComponent(panel, layout, spinnerMinTemp, 1, 1, 1, 1, 1.0, 0, gbc);
		
		
		/* Maximum temperature */
		JLabel maxTempLabel = new JLabel("Choose the maximal temperature of the stable"); // MaxTemp
		maxTempLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addComponent(panel, layout, maxTempLabel, 0, 3, 1, 1, 0, 0, gbc);
		
		JLabel maxTempDesc = new JLabel("(The air condition will be turned on if the temperature exceeds this limit)"); 
		maxTempDesc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addComponent(panel, layout, maxTempDesc, 0, 4, 1, 1, 0, 0, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		spinnerMaxTemp = new JSpinner();
		if(maxTemp == 0) spinnerMaxTemp.setModel(new SpinnerNumberModel(21, 10, 30, 1));
		else spinnerMaxTemp.setModel(new SpinnerNumberModel(maxTemp, 10, 30, 1));
		addComponent(panel, layout, spinnerMaxTemp, 1, 3, 1, 1, 1.0, 0, gbc);
		gbc.fill = GridBagConstraints.NONE;
		
		
		/* Tolerance */
		JLabel toleranceLabel = new JLabel("Choose the temperature tolerance"); // Tolerance
		toleranceLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addComponent(panel, layout, toleranceLabel, 0, 5, 1, 1, 0, 0, gbc);
		
		JLabel toleranceLabelDesc = new JLabel("(Heating/Air condition will be turned off, if the value exceeds the tolerance)");
		toleranceLabelDesc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addComponent(panel, layout, toleranceLabelDesc, 0, 6, 1, 1, 1.0, 0, gbc);
		
		toleranceSpinner = new JSpinner();
		if(tolerance == 0) toleranceSpinner.setModel(new SpinnerNumberModel(0.5, 0.2, 5, 0.1));
		else toleranceSpinner.setModel(new SpinnerNumberModel(tolerance, 0.2, 5, 0.1));
		editor = new JSpinner.NumberEditor(toleranceSpinner);
		toleranceSpinner.setEditor(editor);	
		addComponent(panel, layout, toleranceSpinner, 1, 5, 1, 1, 1.0, 0, gbc);
		
		/* Maximum Body Temperature */
		JLabel bodyTempLabel = new JLabel("Choose the maximal body temperature"); // Tolerance
		bodyTempLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addComponent(panel, layout, bodyTempLabel, 0, 7, 1, 1, 0, 0, gbc);
		
		JLabel bodyTempDesc = new JLabel("(You will receive an alert if one animal's body temperature exceeds this limit)");
		bodyTempDesc.setFont(new Font("Tahoma", Font.PLAIN, 10));
		addComponent(panel, layout, bodyTempDesc, 0, 8, 1, 1, 1.0, 0, gbc);
		
		bodyTempSpinner = new JSpinner();
		if(maxBodyTemp == 0) bodyTempSpinner.setModel(new SpinnerNumberModel(40.0, 38.0, 42.0, 0.1));
		else bodyTempSpinner.setModel(new SpinnerNumberModel(maxBodyTemp, 38.0, 42.0, 0.1));
		editor = new JSpinner.NumberEditor(bodyTempSpinner);
		bodyTempSpinner.setEditor(editor);	
		addComponent(panel, layout, bodyTempSpinner, 1, 7, 1, 1, 1.0, 0, gbc);
		
		/* Button to save the values */
		btnSave = new JButton("Save values");
		btnSave.addActionListener(this);
		addComponent(panel, layout, btnSave, 0, 9, 2, 1, 1.0, 0, gbc);
			
		add(panel);
		setVisible(true);
			
	}

	/**
	 * main-method, just for testing purposes
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InitializationWindow window = new InitializationWindow();
					window.display();
					window.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Actionlistener for the button to save the values
	 * Checks if the entered values are valid and saves them within the appropriate variables if they are
	 * 
	 * @param: ae ActionEvent that was performed
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent ae) {
		int minTemp = (Integer) spinnerMinTemp.getValue();
		int maxTemp = (Integer) spinnerMaxTemp.getValue();
		if(minTemp == maxTemp) { 				// Invalid values
			panelError1 = new JPanel();
			panelError1.add(new JLabel("Error: minimal temperature and maximal temperature cannot be equal!"));
			int error1 = JOptionPane.showConfirmDialog(null, panelError1, "Invalid initialization values ", JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else if(minTemp > maxTemp) { 			// Invalid values
			panelError2 = new JPanel();
			panelError2.add(new JLabel("Error: minimal temperature must be less than maximal temperature!"));
			int error2 = JOptionPane.showConfirmDialog(null, panelError2, "Invalid initialization values ", JOptionPane.CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		} else { 								// Valid values
			EnvironmentPi.minTemp = minTemp;
			EnvironmentPi.maxTemp = maxTemp;
			EnvironmentPi.tolerance = (double)toleranceSpinner.getValue();
			WearablePi.maxBodyTemp = (double)bodyTempSpinner.getValue();
			this.hide();
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

}



