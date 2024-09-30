import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MySimpleServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)){
            // closing a server socket does not close socket connections but it will not accept new connections.
            // It doesn't automatically close existing connections and streams
            // input and output streams will close if nested in another try with resources statement like below
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Server accepted");
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String echoLine = input.readLine();
                    System.out.println("Server received: " + echoLine);
                    if (echoLine.equals("bye")) {
                        break;
                    }
                    output.println("Echo: " + echoLine);
                }
            }
        } catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }
}

// The Typical TCP/IP Client-Server Interaction
    // The Server needs to first create a ServerSocket, and bind it to a port
    // The Server calls accept() on the ServerSocket, which returns a Socket when a client connects
    // The Client creates a Socket using a constructor that takes a host and port.
    // The Server and Client use their respective sockets to exchange data

// only runs one client unless edit configurations to run multiple instances.
// even then the server only accepts the first socket