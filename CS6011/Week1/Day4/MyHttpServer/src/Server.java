import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String requestLine = in.readLine();
            System.out.println("Request: " + requestLine);

            if (requestLine.startsWith("GET /index.html HTTP/1.1")) {
                // Read index.html file
                File indexFile = new File("./resources/index.html");
                if (indexFile.exists()) {
                    BufferedReader fileReader = new BufferedReader(new FileReader(indexFile));
                    String line;

                    // Send HTTP response headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println();

                    // Send the content of index.html
                    while ((line = fileReader.readLine()) != null) {
                        out.println(line);
                    }

                    fileReader.close();
                } else {
                    // Send 404 error if file not found
                    out.println("HTTP/1.1 404 Not Found");
                    out.println();
                }
            }

            clientSocket.close();
        }
    }
}