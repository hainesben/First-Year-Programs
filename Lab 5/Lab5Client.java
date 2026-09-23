import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

/*
Assignment: Lab 4/5
Date: 4/29/2026
Name: Benjamin Haines and Collin Klepikow
Email: ben.haines@tcu.edu & c.klepikow@tcu.edu
Class-Section: COSC 20203
Overview: This is the Client Part. Uses port 25413 and the server's IP adress to establish a socket connection. 
The client converts user's GUI selections into a formatted command String.
Then, sends the command to the server using PrintWriter.
Buffered Reader is used by the client to read the calculated result and display it in the interface.
*/

public class Lab5Client extends JFrame implements ActionListener {
	static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		Lab5Client scc = new Lab5Client();
		scc.setVisible(true);
	}
	JRadioButton dieselButton;
	JRadioButton steamButton;
	ButtonGroup locomotiveGroup;

	JRadioButton cabooseButton;
	JRadioButton presidentialButton;
	JRadioButton firstClassButton;
	JRadioButton openAirButton;
	ButtonGroup seatingGroup;

	JLabel adultLabel;
	JTextField adultTF;
	JLabel childrenLabel;
	JTextField childrenTF;

	JButton calcButton;
	JLabel ansLabel;
	JTextField ansTF;
	
	JLabel errorLabel;

	public Lab5Client() {
		setTitle("Price Calculator");
		setBounds(100, 100, 320, 280);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		dieselButton = new JRadioButton("Diesel");
		dieselButton.setBounds(25, 24, 100, 16);
		dieselButton.setSelected(true);
		getContentPane().add(dieselButton);

		steamButton = new JRadioButton("Steam");
		steamButton.setBounds(25, 64, 100, 16);
		getContentPane().add(steamButton);

		locomotiveGroup = new ButtonGroup();
		locomotiveGroup.add(dieselButton);
		locomotiveGroup.add(steamButton);

		cabooseButton = new JRadioButton("Caboose");
		cabooseButton.setBounds(170, 14, 100, 16);
		cabooseButton.setSelected(true);
		getContentPane().add(cabooseButton);

		presidentialButton = new JRadioButton("Presidential");
		presidentialButton.setBounds(170, 34, 140, 16);
		getContentPane().add(presidentialButton);

		firstClassButton = new JRadioButton("First Class");
		firstClassButton.setBounds(170, 54, 140, 16);
		getContentPane().add(firstClassButton);

		openAirButton = new JRadioButton("Open Air");
		openAirButton.setBounds(170, 74, 140, 16);
		getContentPane().add(openAirButton);

		seatingGroup = new ButtonGroup();
		seatingGroup.add(cabooseButton);
		seatingGroup.add(presidentialButton);
		seatingGroup.add(firstClassButton);
		seatingGroup.add(openAirButton);

		adultLabel = new JLabel("Adults");
		adultLabel.setBounds(30, 104, 50, 16);
		getContentPane().add(adultLabel);
		adultLabel.setVisible(false);

		adultTF = new JTextField();
		adultTF.setBounds(80, 104, 50, 16);
		getContentPane().add(adultTF);
		adultTF.setVisible(false);

		childrenLabel = new JLabel("Children");
		childrenLabel.setBounds(160, 104, 60, 16);
		getContentPane().add(childrenLabel);
		childrenLabel.setVisible(false);

		childrenTF = new JTextField();
		childrenTF.setBounds(225, 104, 50, 16);
		getContentPane().add(childrenTF);
		childrenTF.setVisible(false);

		calcButton = new JButton("Calculate");
		calcButton.setBounds(100, 144, 80, 16);
		getContentPane().add(calcButton);

		ansLabel = new JLabel("Answer");
		ansLabel.setBounds(80, 184, 50, 16);
		getContentPane().add(ansLabel);

		ansTF = new JTextField();
		ansTF.setBounds(150, 184, 75, 16);
		getContentPane().add(ansTF);
		ansTF.setEditable(false);
		
		errorLabel = new JLabel("");
		errorLabel.setBounds(40, 220, 200, 16);
		getContentPane().add(errorLabel);

		cabooseButton.addActionListener(this);
		presidentialButton.addActionListener(this);
		firstClassButton.addActionListener(this);
		openAirButton.addActionListener(this);
		calcButton.addActionListener(this);
		
		connectToServer();
	}

	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Diesel") || action.equals("Steam")) {
			ansTF.setText(""); 
			errorLabel.setText("");
		}
		else if (action.equals("Caboose")) {
			ansTF.setText("");
			errorLabel.setText("");
			adultLabel.setVisible(false);
			adultTF.setVisible(false);
			childrenLabel.setVisible(false);
			childrenTF.setVisible(false);
		} else if (action.equals("Presidential") || action.equals("First Class") || action.equals("Open Air")) {
			ansTF.setText("");
			errorLabel.setText("");
			adultLabel.setVisible(true);
			adultTF.setVisible(true);
			childrenLabel.setVisible(true);
			childrenTF.setVisible(true);
		} else if (action.equals("Calculate")) {
			calculate();
		} 
	}

// ========================================================================
	
// Do not change anything above this line
// Two global variables have been defined here and you will need to add more
	final static String server = "127.0.0.1";
	final static int port = 25413;

	Socket socket;
	BufferedReader is;
	PrintWriter os;

// Then implement the following methods

// The following method connects to the ShapeCalcultorServer
	void connectToServer() {
		try {
			//Establishes connection to the server
			socket = new Socket(server, port);
			//Reads result from server
			is = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			//Sends information to the server
			os = new PrintWriter (new BufferedOutputStream(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			errorLabel.setText("Cannot locate Lab4Server");
			System.exit(1);
		} catch (IOException e) {
			errorLabel.setText("Lab4Server not responding");
			System.exit(1);
		}
	}

// The following method sends an appropriate command to the server
// Then reads the result and displays it in the answer text field
	void calculate() {
		try {
			//Resets text filed of both error and result
			errorLabel.setText("");
			ansTF.setText("");
			
			String locomotive, seat, command, adultStr, childStr, resultLine;

			//Accepts the locomotive input and converts it into a String
			if (dieselButton.isSelected()) locomotive = "Diesel";
			else if (steamButton.isSelected()) locomotive = "Steam";
			else throw new IllegalArgumentException("Select a locomotive");
			
			//Accepts the seat type input and coverts it into a String
			if (cabooseButton.isSelected()) seat = "Caboose";
			else if (presidentialButton.isSelected()) seat = "Presidential";
			else if (firstClassButton.isSelected()) seat = "FirstClass";
			else if (openAirButton.isSelected()) seat = "OpenAir";
			else throw new IllegalArgumentException ("Select a seat");

			//Determines whether  the user selected Caboose as the seat type
			// If so, command includes only locomotive & seat
			// If not, command includes locomotive, seat type, adult #, child # 
			if (seat.equals("Caboose")) command = locomotive + " " + seat;
			else {
				adultStr = adultTF.getText().trim();
				if (adultStr.isEmpty()) throw new IllegalArgumentException ("Enter quantity for adult");
				int numAdult = Integer.parseInt(adultStr);

				childStr = childrenTF.getText().trim();
				if (childStr.isEmpty()) throw new IllegalArgumentException ("Enter quantity for child");
				int numChild = Integer.parseInt(childStr);

				command = locomotive + " " + seat + " " + numAdult + " " + numChild;
			}

			//Print the command to the server
			os.println(command);
			os.flush();

			//Read the result from the server
			resultLine = is.readLine();
			ansTF.setText(resultLine);

		} catch (IllegalArgumentException e) {
			errorLabel.setText("Error: " + e.getMessage());
		} catch (IOException e) {
			errorLabel.setText("I/0 error: " + e.getMessage());
		}
	}
}
