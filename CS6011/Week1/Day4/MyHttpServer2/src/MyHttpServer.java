import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


class HttpRequest {

    Scanner input = new Scanner(System.in);

    String request = input.nextLine();

    String[] parts = request.split(" ");
};

class HttpResponse {

    PrintWriter output = new PrintWriter(System.out, true);

    File file = new File("./resources/index.html");

    FileInputStream fileInputStream = null;

    fileInputStream = new FileInputStream(file);

    byte[] fileData = new byte[(int) file.length()];

    fileInputStream.read(fileData);

    output.println("HTTP/1.1 200 OK");
    output.println("Content-Type: text/html");
    output.println("Content-Length: "+fileData.length);

    output.println();

};

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        // create server socket for client (web browser) to interact with
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            // print in the console that the server is listening for client requests
            System.out.println("Server live on port 8080");
            // while the server is still running
            while (true) {
                Socket clientSocket = serverSocket.accept();
            }
        }
    }
}