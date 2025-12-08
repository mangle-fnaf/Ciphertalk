package p;

import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.*;

public class ChatServer {
	//entry point and if it fails, it throws exception
	public static void main(String[] args) throws IOException {
		int port = 7654;
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}
		//locates server key store
		//server can prove its identity when client connects
		//without proof of identity, the server won't establish SSL/TLS connection
		System.setProperty("javax.net.ssl.keyStore", "Keystore/serverkeystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "ruler95!");
		
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(port);
		System.out.println("Secure Server is running" + port + " ...");
		
		//server keeps running
		while (true) {
			//once client connects,returns socket
			SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
			clientSocket.setEnabledProtocols(new String [] {"TLSv1.2", "TLSv1.3"});
			System.out.println("New user connected." + clientSocket);
			new ClientThread(clientSocket).start();
		}
	}
}