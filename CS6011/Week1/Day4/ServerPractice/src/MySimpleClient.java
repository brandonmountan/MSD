import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MySimpleClient {
    public static void main(String[] args) throws IOException {

        try (Socket socket = new Socket("localhost", 8080)) {
            // a way to pass data and get data back from the other end point
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // autoflush true will flush the buffer after any print statement
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            // scanner variable will accept input from the user via the keyboard
            Scanner scanner = new Scanner(System.in);
            // request we'll pass to the server
            String requestString;
            // set up variable for data we get back from server
            String responseString;
            // do-while instead because i'll always send at least one request
            do {
            // prompt the user to enter a string
                System.out.println("Enter string to be echoed (sent to server): ");
                requestString = scanner.nextLine();
                // sends request string to server
                output.println(requestString);
                if (!requestString.equals("exit")) {
                    responseString = input.readLine();
                    System.out.println(responseString);
                }
              // keep prompting user for input and send to server as long as client doesn't type "exit"
            } while (!requestString.equals("exit"));
        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected.");
        }

    }
}