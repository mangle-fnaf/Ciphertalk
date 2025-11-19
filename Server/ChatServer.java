import java.net.*;
import java.io.*;

public class ChatServer {
  public static void main(String[] args) throws IOException {
    int port number = xyz;

  try (
    ServerSocket serversocket = new ServerSocket(port);
    Socket clientSocket = serverSocket.accept();
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    
