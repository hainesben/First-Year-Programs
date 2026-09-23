/*
Assignment: Lab 1 
Date: 2/10/2026
Name: Benjamin Haines
Email: ben.haines@tcu.edu
Class-Section: COSC 20203
Overview: 
This program represents an IBM machine that helps encode Assembly Instructions and displays results in Binary and Hex. Along with
user decoding Binary which results in Hex and Assembly language. Along with the user decoding Hex which results in Binary
and Assembly language.
*/
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;

public class Lab1 extends JFrame implements ActionListener {
	private JTextField assemblerInstruction;
	private JTextField binaryInstruction;
	private JTextField hexInstruction;
	private JLabel errorLabel;
	
	public Lab1() {
		setTitle("IBM System/360");
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE ASSEMBLY LANGUAGE TEXTFIELD AND BUTTON
		assemblerInstruction = new JTextField();
		assemblerInstruction.setBounds(25, 24, 134, 28);
		getContentPane().add(assemblerInstruction);

		JLabel lblAssemblyLanguage = new JLabel("Assembly Language");
		lblAssemblyLanguage.setBounds(30, 64, 160, 16);
		getContentPane().add(lblAssemblyLanguage);

		JButton btnEncode = new JButton("Encode");
		btnEncode.setBounds(200, 25, 117, 29);
		getContentPane().add(btnEncode);
		btnEncode.addActionListener(this);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE BINARY INSTRUCTION TEXTFIELD AND BUTTON
		binaryInstruction = new JTextField();
		binaryInstruction.setBounds(25, 115, 330, 28);
		getContentPane().add(binaryInstruction);

		JLabel lblBinary = new JLabel("Binary Instruction");
		lblBinary.setBounds(30, 155, 190, 16);
		getContentPane().add(lblBinary);

		JButton btnDecode = new JButton("Decode Binary");
		btnDecode.setBounds(200, 150, 150, 29);
		getContentPane().add(btnDecode);
		btnDecode.addActionListener(this);
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE HEX INSTRUCTION TEXTFIELD AND BUTTON
		hexInstruction = new JTextField();
		hexInstruction.setBounds(25, 220, 134, 28);
		getContentPane().add(hexInstruction);

		JLabel lblHexEquivalent = new JLabel("Hex Instruction");
		lblHexEquivalent.setBounds(30, 260, 131, 16);
		getContentPane().add(lblHexEquivalent);

		JButton btnDecodeHex = new JButton("Decode Hex");
		btnDecodeHex.setBounds(200, 220, 150, 29);
		getContentPane().add(btnDecodeHex);
		btnDecodeHex.addActionListener(this);		
// * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// SET UP THE LABEL TO DISPLAY ERROR MESSAGES
		errorLabel = new JLabel("");
		errorLabel.setBounds(25, 320, 280, 16);
		getContentPane().add(errorLabel);
	}

	public void actionPerformed(ActionEvent evt) {
		errorLabel.setText("");
		if (evt.getActionCommand().equals("Encode")) {
			encode();
		} else if (evt.getActionCommand().equals("Decode Binary")) {
			decodeBin();
		} else if (evt.getActionCommand().equals("Decode Hex")) {
			decodeHex();
		}
	}

	public static void main(String[] args) {
		Lab1 window = new Lab1();
		window.setVisible(true);
	}

// USE THE FOLLOWING METHODS TO CREATE A STRING THAT IS THE
// BINARY OR HEX REPRESENTATION OF A SHORT OR INT

// CONVERT A SHORT TO 4 HEX DIGITS
	String displayShortAsHex(short x) {
		String ans="";
		for (int i=0; i<4; i++) {
			int hex = x & 15;
			char hexChar = "0123456789ABCDEF".charAt(hex);
			ans = hexChar + ans;
			x = (short)(x >> 4);
		}
		return ans;
	}

// CONVERT A SHORT TO 16 BINARY DIGITS
	String displayShortAsBinary(short x) {
		String ans="";
		for(int i=0; i<16; i++) {
			ans = (x & 1) + ans;
			x = (short)(x >> 1);
		}
		return ans;
	}

// CONVERT AN INT TO 8 HEX DIGITS
	String displayIntAsHex(int x) {
		String ans="";
		for (int i=0; i<8; i++) {
			int hex = x & 15;
			char hexChar = "0123456789ABCDEF".charAt(hex);
			ans = hexChar + ans;
			x = (x >> 4);
		}
		return ans;
	}

// CONVERT AN INT TO 32 BINARY DIGITS
	String displayIntAsBinary(int x) {
		String ans="";
		for(int i=0; i<32; i++) {
			ans = (x & 1) + ans;
			x = (x >> 1);
		}
		return ans;
	}

	
/************************************************************************/
/* Put your implementation of the encode, decodeBin, and decodeHex      */
/* methods here. You may add any other methods that you think are       */
/* appropriate. However, you MUST NOT change anything in the code       */
/* that I have written.                                                 */
/************************************************************************/
	void encode() {
		//Resets the text labels
		errorLabel.setText("");
		binaryInstruction.setText("");
		hexInstruction.setText("");
		
		//variable for RX, used for ST, L, A instructions
		int rxinstruction = 0;
		//variable used for AR instruction
		short rrInstruction = 0;
		//gets what user input from the assembler text field
		String uInput = assemblerInstruction.getText();
		//error checking for if user didn't put anything in the text field
		if (uInput.isEmpty()) {
			errorLabel.setText("ERROR - empty space");
			return;
		}
		//get the position where the space is
		int blankPosition = uInput.indexOf(" ");
		//makes sure that user has a space and has operands to go with the instruction
		if (blankPosition < 1 || blankPosition == uInput.length() - 1) {
			errorLabel.setText("ERROR - Invalid format: no space or operand");
		}
		//gets the instruction value, AR, S, L, or A
		String operation = uInput.substring(0, blankPosition);
		//gets the numbers that are part of the instruction like the R,S or R,D(X,B)
		String operands = uInput.substring(blankPosition).trim();
		//boolean used later to identify whether or not it's AR method
		boolean isRR = false;
		
		//big try catch statement to see if the number format is valid or string format is valid
		try{
			//if statement to check is instruction value is AR and turns isRR to true
			if (operation.equals("AR")) {
				isRR = true;
				//try-catch statement to see if user put in the values within the range
				try {
					//puts the numbers in method for AR
					rrInstruction = ParseMethodAR(operands); 
				} catch (IllegalArgumentException e) {
					errorLabel.setText(e.getMessage());
					return;
				}
			}
			//if statement to check is instruction value is ST
			else if (operation.equals("ST")){
				//we take the opcode for ST and have it shift left 24 bits to have it in 8 left most bits
				//then we use the OR symbol to keep the opcode shifted and not erase the other fields (X, B, D)
				rxinstruction = rxinstruction | (0x50 << 24);
				//try-catch statement to see if user put in the values within the range
				try {
					//puts the numbers in method for ST
					rxinstruction = ParseMethodLAST(rxinstruction, operands);

				} catch (IllegalArgumentException e) {
					errorLabel.setText(e.getMessage());
					return;
				}
			}
			//same as ST
			else if (operation.equals("A")){
				//same as ST
				rxinstruction = rxinstruction | (0x5A << 24);
				//same as ST
				try {
					//same as ST
					rxinstruction = ParseMethodLAST(rxinstruction, operands);

				} catch (IllegalArgumentException e) {
					errorLabel.setText(e.getMessage());
					return;
				}
			}
			//same as ST all the way through the end of this else if
			else if (operation.equals("L")){
				rxinstruction = rxinstruction | (0x58 << 24);
				try {
					rxinstruction = ParseMethodLAST(rxinstruction, operands);

				} catch (IllegalArgumentException e) {
					errorLabel.setText(e.getMessage());
					return;
				}
			}
			//if operation isn't any of the 4 instructions, then sends error to user for invalid mnemonic (S or AL is invalid)
			else{
				errorLabel.setText("ERROR - invalid mnemonic");
				return;

			}
		}
		//catches whether or not numbers is weird L 5,abc(0,12) or format is weird A ,532(10,15)
		catch (NumberFormatException | StringIndexOutOfBoundsException e){
		errorLabel.setText("ERROR - invalid numbers or format");
		return;
		}
		//if isRR is set to true, then rrinstruction is put in the short methods for both hex and binary
		if (isRR == true) {
			binaryInstruction.setText(displayShortAsBinary(rrInstruction));
			hexInstruction.setText(displayShortAsHex(rrInstruction));
		}
		//any other instructions (ST, L, and A) are put into the int methods
		else{
			String assembly2B = displayIntAsBinary(rxinstruction);
			String assembly2H = displayIntAsHex(rxinstruction);
			binaryInstruction.setText(assembly2B);
			hexInstruction.setText(assembly2H);
		}
	
	}

	void decodeBin() {
		errorLabel.setText("");
		assemblerInstruction.setText("");  
    	hexInstruction.setText("");
		//gets the text user inputted and puts that text into a String value
		String uInput = binaryInstruction.getText().trim();
		//if statement for error checking if user didn't put anything in the text field
		if (uInput.isEmpty()) {
			errorLabel.setText("ERROR - empty space");
			return;
		}
		//if the length isn't 32 bits and isn't 16 bits, then errors the user 
		if (uInput.length() != 32 && uInput.length() != 16) {
			errorLabel.setText("ERROR - must have 32 bits or 16 bits");
			return;
		}

		//try-catch statement on parseint to make sure number format is correct 
		//for binary
		int instruction = 0;
		try {
		instruction = Integer.parseInt(uInput, 2);
		} catch (NumberFormatException e) {
		errorLabel.setText("ERROR - not binary");
		}

		//we get the inital opcode by checking length and if it's 16 bits or 32
		int opcodeShift = (uInput.length() == 16) ? 8 : 24;
		//it'll then shift right to get rid of the the 8 bits or 24 bits
		//this then leads to the AND symbol to mask the 8 bits (0xFF) that have our opcode
		int opcode = (instruction >> opcodeShift) & 0xFF;
		//big if else statement to check if it's AR (16 bits) or ST, L, A (32 bits)
		if (uInput.length() == 16) {
			//checks error if the opcode doesn't equal hexadecimal 1A
			if (opcode != 0x1A) {
				errorLabel.setText("ERROR - invalid opcode");
				return;
			}
			//shifts right the opcode 4 bits and puts it into variable R
			int R = (instruction >> 4) & 0xF;
			//doesn't shift right since its the last 4 bits and puts it into variable S
    		int S = instruction & 0xF;
			//sets the text of AR with the corresponding variables
    		assemblerInstruction.setText("AR " + R + "," + S);
			//cast short onto instruction to make it display a 4 digit variable 
			//puts that casted instruction into the hex short method and displays to user
    		hexInstruction.setText(displayShortAsHex((short) instruction));  

		}
		else{ 
			//do calculations for R, X, B, D to make the process cleaner and smoother
			//shifting right every 4 bits and using AND symbol to keep them in this case
			int R = (instruction >> 20) & 0xF;
			int X = (instruction >> 16) & 0xF;
			int B = (instruction >> 12) & 0xF;
			//To keep last 12 bits we use 0xFFF (12 bits) along with the AND symbol to keep them in D
			int D = instruction & 0xFFF;
			//String used later to display assembly instructions
			String assembly;
			//switch statement for opcode
			switch (opcode) {
				//all these cases will then put the corresponding variable into the String assembly 
				//to be used later on to display to the user
				case 0x58:  // L assembler
					assembly = "L " + R + "," + D + "(" + X + "," + B + ")";
					break;
				case 0x5A:  // A assembler
					assembly = "A " + R + "," + D + "(" + X + "," + B + ")";
					break;
				case 0x50:  // ST assembler
					assembly = "ST " + R + "," + D + "(" + X + "," + B + ")";
					break;
					//default statement to display error incase the opcode isn't a valid 32 bit opcode
				default:
					errorLabel.setText("ERROR - invalid 32-bit opcode");
					return;  
			}
			//put the String value of assembly into the assembler instruction
			assemblerInstruction.setText(assembly);
			//puts the value of instruction after the right shifts with AND symbols and 
			//converts it into hex int and displays it to user.
			hexInstruction.setText(displayIntAsHex(instruction));  
		}
	}

	void decodeHex() {
		errorLabel.setText("");
		assemblerInstruction.setText(""); 
    	binaryInstruction.setText("");
		//trims user input from hex instruction field and automatically upper cases the users letters
		String uInput = hexInstruction.getText().trim().toUpperCase();
		//if statement for error checking if user didn't put anything in the text field
		if (uInput.isEmpty()) {
			errorLabel.setText("ERROR - Empty Space");
			return;
		}
		//checks for length of user input, if its not 8 digits AND not 4 digits, error is thrown
		//to the user for not having enough hexadecimals
		if (uInput.length() != 8 && uInput.length() != 4) {
			errorLabel.setText("ERROR - must be 4 hexadecimals or 8");
			return;
		}
		//try-catch statement on parseint to make sure number format is correct 
		//for hex
		int instruction;
		try {
			instruction = Integer.parseInt(uInput, 16);
		} catch (NumberFormatException e) {
			errorLabel.setText("ERROR - invalid hexadecimal characters");
			return;
		}
		//checks if user input is 4 and shifts right accordingly
		//if it is 4 then it'll shift 8 bits, if not, it'll shift 24 bits
		int opcodeShift = (uInput.length() == 4) ? 8 : 24;
		//shifts right according to user input and keeps the last 8 bits with & symbol
		int opcode = (instruction >> opcodeShift) & 0xFF;
		//big if else statement, same as decode binary but this first one
		//checks to see if the user input 4 digits
		if (uInput.length() == 4) {
			//checks error for invalid opcode
			if (opcode != 0x1A) {
				errorLabel.setText("ERROR - invalid opcode");
				return;
			}
			//same as decode binary
			int R = (instruction >> 4) & 0xF;
    		int S = instruction & 0xF;
    		assemblerInstruction.setText("AR " + R + "," + S);
			//same as decode binary except we have hex value so input the 
			//hex value into binary short method and display as binary
    		binaryInstruction.setText(displayShortAsBinary((short) instruction));  

		}
		else{ 
			//same as decode binary all the way down to .setText for assemblerinstruction
			int R = (instruction >> 20) & 0xF;
			int X = (instruction >> 16) & 0xF;
			int B = (instruction >> 12) & 0xF;
			int D = instruction & 0xFFF;

			String assembly;

			switch (opcode) {
				case 0x58:  // L assembler
					assembly = "L " + R + "," + D + "(" + X + "," + B + ")";
					break;
				case 0x5A:  // A assembler
					assembly = "A " + R + "," + D + "(" + X + "," + B + ")";
					break;
				case 0x50:  // ST assembler
					assembly = "ST " + R + "," + D + "(" + X + "," + B + ")";
					break;
				default:
					errorLabel.setText("ERROR - invalid 32-bit opcode");
					return;  
			}

			assemblerInstruction.setText(assembly);
			//no need to case as it's automatically an int and 
			//put it in the binary int method and display as binary
			binaryInstruction.setText(displayIntAsBinary(instruction));  
		}
	}
	//method to parse AR user input for numbers
	short ParseMethodAR(String operands){
		//finds out where the comma is in the user instructions
		int comma = operands.indexOf(',');
		//finds the number in the R positions and S position and trims leading or trailing spaces
		String rOperand = operands.substring(0, comma).trim();
		String sOperand = operands.substring(comma + 1).trim();
		//parses the string into a short value and puts them in a short value for the corresponding letter
		short R = Short.parseShort(rOperand);
		short S = Short.parseShort(sOperand);
		//if statements to throw error if range is invalid
		if (R < 0 || R > 15) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register R");
		}
		if (S < 0 || S > 15) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register S");

		}
		//create rrinstruction and inputs the binary numbers
		short rrinstruction;
		//casting hex value of 1A into a short so it's a 16 bit instead of 32 bit
		//shifts the 8 bits into the upper most part of the binary code
		rrinstruction = (short) (0x1A << 8);
		//shifts the R value into the next 4 bits and user OR symbol to keep it there
		rrinstruction |= (R << 4);
		//no need for shift as it occupies the last 4 bits, but do inclue OR to keep it there as well
		rrinstruction |=  S;

		return rrinstruction;
	}
	//method to parse users input for instructions LT, S, and A
	int ParseMethodLAST(int instruction, String operands){
		StringTokenizer st = new StringTokenizer(operands, " ,()");
		//index methods; first one finds the comma
		int comma = operands.indexOf(',');
		//then find the beginning paretheses
		int parentheses = operands.indexOf('(');
		//finds the second comma, to have no confusion from finding first comma, 
		//finds it after the first parentheses
		int comma2 = operands.indexOf(',', parentheses);
		//finally finds the end parentheses
		int endparentheses = operands.indexOf(')');
		//finds the string of the values user inputted, remember, sub string is inclusive of the first
		//value so it includes it but excludes the last value, which means it doesn't add it to the string
		String rOperand = operands.substring(0, comma).trim();
		//that's why it's required to have + 1 in the code after finding the first R value
		String dOperand = operands.substring(comma + 1, parentheses).trim();
		String xOperand = operands.substring(parentheses + 1, comma2).trim();
		String bOperand = operands.substring(comma2 + 1, endparentheses).trim();
		//parses the Strings into integers as that's required for the lab
		int R = Integer.parseInt(rOperand);
		int D = Integer.parseInt(dOperand);
		int X = Integer.parseInt(xOperand);
		int B = Integer.parseInt(bOperand);
		//4 if statements to see if the R, X, D, and B values are within range
		if (R < 0 || R > 15) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register R");
		}
		if (X < 0 || X > 15) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register X");

		}
		if (B < 0 || B > 15) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register B");

		}
		if (D < 0 || D > 4095) {
			throw new IllegalArgumentException("ERROR - Invalid Range for Register D");

		}
		//shifts left going 4 bits at a time to input the bits into instruction variable
		//then uses OR symbol to keep them their
		instruction = instruction | (R << 20);
		instruction = instruction | (X << 16);
		instruction = instruction | (B << 12);
		//no need to shift for D since it's the last one and has 12 bits on its own
		instruction = instruction | D;

		return instruction; 
	}
	

}
