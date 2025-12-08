package p;

import java.io.*;
import java.net.*;

public class ChatClient {
	
	//entry point and throws exceptions if anything goes wrong
	public static void main(String[] args) throws IOException {
		//Makes sure user provides the correct number of arguments
		if (args.length != 2) {
			//prints usage instructions if not
			System.out.println("Usage: java QuoteClient <hostname>");
            return;
		}
		
		//reads host name and port number 
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		
		//opens TCP connection
		Socket socket = new Socket(hostname, port);
		
		//from server-read text lines sent by server (wraps socket input)
		BufferedReader ServerIn =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
		
		//(wraps socket output)- messages sent to server immediately
		PrintWriter ServerOut =
                new PrintWriter(socket.getOutputStream(), true);
		
		//read inputs from user 
		BufferedReader UserInput =
                new BufferedReader(
                    new InputStreamReader(System.in));
		
		//The server prints the user name. It reads the user name and sends it to the server.
		System.out.println(ServerIn.readLine());
		String username = UserInput.readLine();
		ServerOut.println(username);
		
		//starts new thread (continuous)
		new Thread(() -> {
			try {
				String message;
				//continuously reads inputs sent by server.
				//If the server is closed,the loop ends(null).
				while ((message = ServerIn.readLine()) != null) {
					System.out.println(message);
				}
			}catch (IOException e) {
				System.out.println("disconnected from server.");
			}
		}).start();
		
		String input;
		//Continuously reads inputs until program is closed.
		while ((input = UserInput.readLine()) != null) {
			//checks if user ends session so it breaks out of loop
			if (input.equalsIgnoreCase("/quit")) break;
			 ServerOut.println(input);
		}
		//signals to server that client has disconnected and proceeds to end system.
		socket.close();
	}
}
