import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 8080);

//        Scanner in = new Scanner(System.in);

        String message = "Hello Network, I am the client";

        socket.getOutputStream().write((message + "\n").getBytes()); //convert message to bytes

        InputStream input = socket.getInputStream();

        Scanner in = new Scanner(input);

        while (socket.isConnected()) {

            System.out.println(in.nextLine());

        }
    }
}