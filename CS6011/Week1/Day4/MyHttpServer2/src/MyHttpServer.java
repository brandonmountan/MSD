import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)){
            // closing a server socket does not close socket connections but it will not accept new connections.
            // It doesn't automatically close existing connections and streams
            // input and output streams will close if nested in another try with resources statement like below
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Server accepted");
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            }
        } catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }

    }
}