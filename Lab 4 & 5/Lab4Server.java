import java.io.*;
import java.net.*;

/*
Assignment: Lab 4/5
Date: 4/29/2026
Name: Benjamin Haines and Collin Klepikow
Email: ben.haines@tcu.edu & c.klepikow@tcu.edu
Class-Section: COSC 20203
Overview: This is the Server Part. Uses port 25413 and creates a Server to accept the Client's info about train tickets.
Also uses, BufferedReader and PrintWriter to read the Client's info and prints out the total price to client through Printwriter.
*/

public class Lab4Server {
    public static void main(String[] args){
        final int port = 25413;	
        try {
            //Creates server with the port
            ServerSocket server = new ServerSocket(port);
            while (true) {
			    Socket clientSocket;
            //socket allows client to connect to the server
			try {
				clientSocket = server.accept();
			} catch (IOException e) {
				System.out.println("Accept failed: " + port + ", " + e);
				continue;
			}
            //creates the helper thread for the client to immediately access the run method in the thread class
			new Lab4ServerThread(clientSocket).start();
		}
        } catch (IOException e) {
            System.out.println("Server setup failed: " + e);
        }
    }
}


class Lab4ServerThread extends Thread {
    //socket for this specific client connection
	final private Socket socket;
    //constructor for thread class
    Lab4ServerThread(Socket socket) {
		this.socket = socket;
	}
    //method to calculate train price
    private double calculatePrice(String s){
        String[] parts = s.trim().split("\\s+");
        String locomotive = parts[0];
        String seatType = parts [1];
        

        if (seatType.equals("Caboose")) {
            if(locomotive.equals("Diesel")){
                return 900;
            }
            else{
                return 975;
            }
        }
        //if it's not a caboose, parse the last 2 parts into an int and do
        //the necessary calculations for adults and children
        int numadults = Integer.parseInt(parts[2]);
        int numchilds = Integer.parseInt(parts[3]);

        if (seatType.equals("Presidential")) {
            if(locomotive.equals("Diesel")){
                return (85*numadults) + (60*numchilds);
            }
            else{
                return (95*numadults) + (70*numchilds);
            }
        }

        if (seatType.equals("FirstClass")) {
            if(locomotive.equals("Diesel")){
                return (60*numadults) + (35*numchilds);
            }
            else{
                return (70*numadults) + (45*numchilds);
            }
        }

        if (seatType.equals("OpenAir")) {
            if(locomotive.equals("Diesel")){
                return (30*numadults) + (15*numchilds);
            }
            else{
                return (40*numadults) + (25*numchilds);
            }
        }
        return 0;
    }
    //run method using buffreader and printwriter, reads input and calculates output
        @Override
    public void run() {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter os = new PrintWriter(socket.getOutputStream(), true);

			String inputLine;

            while ((inputLine = is.readLine()) != null) {
                double total = calculatePrice(inputLine);
                os.println(total);
            }
            socket.close();
			
		} catch (IOException e) {
			System.out.println("I/O error: " + e);
		}
	}
}
