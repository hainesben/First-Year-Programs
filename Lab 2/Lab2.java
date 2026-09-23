/*
Assignment: Lab 2 
Date: 3/3/2026
Name: Benjamin Haines
Email: ben.haines@tcu.edu
Class-Section: COSC 20203
Overview: 
This program represents a highly simplified programming language that can read and 
simulate the execution of each line, in the input contains one statement. There are no
declaration statements " no ;" and all values are of type double. 
It's also a GUI so you can make your own code to feed into this program and see if it works.
*/
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Lab2 extends JFrame implements ActionListener {
	JButton open = new JButton("Next Program");
	JTextArea result = new JTextArea(20,40);
	JLabel errors = new JLabel();
	JScrollPane scroller = new JScrollPane();
	
	public Lab2() {
		setLayout(new java.awt.FlowLayout());
		setSize(500,430);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(open); open.addActionListener(this);
		scroller.getViewport().add(result);
		add(scroller);
		add(errors);
	}
	
        @Override
	public void actionPerformed(ActionEvent evt) {
		result.setText("");	//clear TextArea for next program
		errors.setText("");
		processProgram();
	}
	
	public static void main(String[] args) {
		Lab2 display = new Lab2();
		display.setVisible(true);
	}
	
	String getFileName() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getPath();
		else
			return null;
	}
	
/************************************************************************/
/* Put your implementation of the processProgram method here.           */
/* Use the getFileName method to allow the user to select a program.    */
/* Then simulate the execution of that program.                         */
/* You may add any other methods that you think are appropriate.        */
/* However, you should not change anything in the code that I have      */
/* written.                                                             */
/************************************************************************/
	public void processProgram(){
		String fileName = getFileName();
		if (fileName != null) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(fileName));
				String line; // lines read
				int lineCount = 0; //counting lines in the given program
				ArrayList <String> userprogram = new ArrayList<>(); // used to evaluate size of program
				HashMap<String, Double> hashMap = new HashMap<>(); //hashmap to store variables and their values
				//while statement reading each line and adding it to the string arraylist
				while ((line = in.readLine()) != null) { 
					userprogram.add(line.trim());
				}
				//big while statement to evaluate user's code
				while (lineCount < userprogram.size()) { 
					//gets lines from user's code and puts it into string current
					String current = userprogram.get(lineCount); //everytime a statement is executed, line count has + 1 added
					if (current.isEmpty()) {
						lineCount++;
						continue;
					}
					//big try catch statement
					try {
						//END code
					if (current.equals("END")) {
						break;
					}
					//PRINT code
					if (current.startsWith("PRINT")) {
						String[] parts = current.trim().split("\\s+");
						if (parts.length != 2) {
                            errors.setText("Line " + (lineCount + 1) + " - ERROR: Invalid PRINT statement");
                            return;
                        }		
						double val = getValue(parts[1], hashMap);
						result.append(String.format("%.2f%n", val));
						lineCount++;
					}
					//GOTO code if line starts with it
					else if (current.startsWith("GOTO")) {
						String[] parts = current.trim().split("\\s+");
						int lineNumber;
						if (parts.length != 2) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Invalid GOTO statement");
                            return;
						}
						try {
							lineNumber = Integer.parseInt(parts[1]);
						} catch (NumberFormatException e) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: GOTO requires a line number");
							return;
						}
						if (lineNumber < 1 || lineNumber > userprogram.size()) {
        					errors.setText("Line " + (lineCount + 1) + " - ERROR: GOTO line number out of range");
                            return;
    					}
        				lineCount = lineNumber - 1; 
					}
					//IF THEN statements
					else if (current.startsWith("IF")) {
						String[] parts = current.trim().split("\\s+");
						if (parts.length != 7 || !parts[4].equals("THEN") || !parts[2].equals("IS")) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Invalid IF statement");
                            return;
						}
						//sees if variable equals a number
						double left = getValue(parts[1], hashMap);
    					double right = getValue(parts[3], hashMap);
						boolean test = left == right;
						int lineNumber;
						//if boolean true, evaluates if the line wants to PRINT or GOTO
						if (test) {
							String action = parts[5];
							String arg = parts[6];
							//switch statement to evalute GOTO and PRINT statement
							switch (action) {
								case "GOTO" -> {
									try {
                    					lineNumber = Integer.parseInt(arg);
                					} catch (NumberFormatException e) {
                    					errors.setText("Line " + (lineCount + 1) + " - ERROR: IF THEN GOTO requires a valid line number");
                                        return;
                					}
									if (lineNumber < 1 || lineNumber > userprogram.size()) {
                                        errors.setText("Line " + (lineCount + 1) + " - ERROR: IF THEN GOTO line number out of range");
                                        return;
                                    }
                                    lineCount = lineNumber - 1;
                                }
								case "PRINT" -> {
									double val = getValue(arg, hashMap);
                					result.append(String.format("%.2f%n", val));
                					lineCount++;
								}
								default -> {
									errors.setText("Line " + (lineCount + 1) + " - ERROR: Unknown action " + action + " in IF statement");
                                    return;
								}
							}
						} else {
							lineCount++;
						}
					}
					//evaluates assignment operator
					else if (current.contains("=")) {
						String[] parts = current.split("=");
						if (parts.length != 2) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Invalid Assignment");
							return;
						}
						String var = parts[0].trim();       
						String expr = parts[1].trim();
						if (var.equals("PRINT") || var.equals("END") || var.equals("GOTO") || var.equals("IF") || var.equals("THEN")) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Can't use reserved word as variable name");
							return;
						}
						//new Tokenizer for evaluating operators like +,-,*,/
						StringTokenizer tok = new StringTokenizer(expr);
						if (!tok.hasMoreTokens()) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Empty expression in assignment");
							return;
						}
						//finds value of the first token
						String first = tok.nextToken();
						double currentval = getValue(first, hashMap);
						while (tok.hasMoreTokens()) { 
							String operator = tok.nextToken();
							if (!tok.hasMoreTokens()) {
							errors.setText("Line " + (lineCount + 1) + " - ERROR: Expression ends with operator");
							return;
							}
							//finds value of next token
							String nextTok = tok.nextToken();
							double nextVal = getValue(nextTok, hashMap);
							//switch statement to see if operator will add, subtract, multiple, or divide first token with the next token
                            switch (operator) {
                                case "+" -> currentval += nextVal;
                                case "-" -> currentval -= nextVal;
                                case "*" -> currentval *= nextVal;
                                case "/" -> {
                                    if (nextVal == 0) {
                                        errors.setText("Line " + (lineCount + 1) + " - ERROR: Divide by 0");
                                        return;
                                    }
                                    currentval /= nextVal;
                                    }
                                 default -> {
                                    errors.setText("Line " + (lineCount + 1) + " - ERROR: Unknown operator " + operator);
                                    return;
								}
							}
						}
						//finally puts the variable with its new value
						hashMap.put(var, currentval);
						lineCount++;
					}
					//final else statement at the very end to evaluate if statement even exists
					else {
						errors.setText("Line " + (lineCount + 1) + " - ERROR: Unknown statement");
                    	return;
					}
					//end of 2nd try catch statement
					}catch(Exception e){
						errors.setText("Line " + (lineCount + 1) + " - ERROR: Runtime error: " + e.getMessage());
                    	return;
					}
				}
				//end of 1st try catch statement 
			} catch (IOException e) {
				errors.setText("ERROR: Faulty File");
			}
		}
	}
	//method to get the value of a token (or argument) with it's hashmap value
	public double getValue(String tokenORarg, Map <String, Double> variables){
		try {
			return Double.parseDouble(tokenORarg);
		} catch (NumberFormatException e) {
			Double value = variables.get(tokenORarg);
			if (value == null) {
				throw new RuntimeException("Undefined variable " + tokenORarg);
			}
			return value;
		}
	}
}
