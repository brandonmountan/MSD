import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        try {

            ServerSocket serverSocket = new ServerSocket(8080);

            while (true) {

                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();

                Scanner scanner = new Scanner(input);

                System.out.println(scanner.nextLine());

                System.out.println(socket.getInetAddress());

                System.out.println(socket.getPort());


                socket.getOutputStream().write("Welcome Client".getBytes());

            }
        }
    }
}