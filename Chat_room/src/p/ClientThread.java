
package p;

import java.net.*;
import java.io.*;
import java.util.*;

//multiple clients can chat at the same time
public class ClientThread extends Thread{
	
	//connection to one client
	private Socket socket;
	//stores client's user name
	private String username;
	//reads input from client
	private BufferedReader in;
	//sends output to client
	private PrintWriter out;
	
	//makes it thread safe
	private static Map<String, String> ConfirmedUsers= Map.of(
			"Maliyka", "M1",
			"Salma", "S1",
			"Amy", "A1",
			"Rachael", "R1"
			);
	private static Map<String, PrintWriter> clients = Collections.synchronizedMap(new HashMap<>());
	
	//saves client's socket when server creates new thread
	public ClientThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			//sets up input and output streams
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			//asks client for user name and reads it
			out.println("Please enter your username:");
			username = in.readLine();
			
			out.println("Please enter your password:");
			String password = in.readLine();
			
			if (!ConfirmedUsers.containsKey(username) ||
				!ConfirmedUsers.get(username).equals(password)) {
					out.println("Your credentials are wrong. Disconnecting");
					socket.close();
					return;
				}
			synchronized (clients) {
				if (clients.containsKey(username)) {
					out.println("Username has already been taken. Disconnecting");
					socket.close();
					return;
				}
				clients.put(username, out);
			}
			
			broadcast(">>" + username + " has joined the chat");
			
			//continuously reads messages from client
			//each message a user sends, it broadcasts their user name and input to everybody
			String message;
			while ((message = in.readLine()) != null) {
				broadcast(username + ":" + message);
			}
			
		}catch (IOException e) {
			System.out.println("User disconnected");
			
		}finally {
			//removes client from shared set
			if (out != null) {
				clients.remove(username);
			}
			System.out.println(username + " has left the chat");
			//if user leaves, it is broadcasted with their user name
			broadcast(">>" + username + " has left the chat.");
			try { 
				socket.close();
			}
			catch (IOException e) {}
		}
	}
	
	//sends message to every client that is connected
	//ensures all clients can type at once 
	private void broadcast(String message) {
		synchronized(clients) {
			for(PrintWriter writer : clients.values()) {
				writer.println(message);
			}
		}
	}
}