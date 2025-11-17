public class ChatClient {
  public static void main(String[] args) throws IOException {

  if (args.length != 2) {
    System.err.println(
      "Usage: java ChatClient <hostname> <port number>");
    System.exit(1);
  }

  String hostname = 12345;
  int portNumber = Integer.parseInt(args[1]);

  try (
    
