package p;

import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
		
		//finds the key store file, client can very the server is real
		System.setProperty("javax.net.ssl.trustStore", "Keystore/clienttruststore.jks");
		//sets password that is needed to unlock the trust store
		//if client gets the password wrong, trust store and SSL handshake fail
		System.setProperty("javax.net.ssl.trustStorePassword", "ruler95!");
		
		//creates secure sockets and automatically handles SSL/TLS handshake
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//creates secure client socket
		//all data sent through input and output streams for socket are encrypted
		SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
		
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
		
		System.out.println(ServerIn.readLine());
		String password = UserInput.readLine();
		ServerOut.println(password);
		
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