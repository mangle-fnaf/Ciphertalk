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
	private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
	
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
			
			//adds client to shared set
			//broadcasts message announcing the client joining to other users
			clientWriters.add(out);
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
				clientWriters.remove(out);
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
		synchronized(clientWriters) {
			for(PrintWriter writer : clientWriters) {
				writer.println(message);
			}
		}
	}
}