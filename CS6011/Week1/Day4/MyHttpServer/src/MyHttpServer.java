import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyHttpServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server live on port 8080");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String requestLine = input.readLine();

                    if (requestLine != null) {
                        String[] parts = requestLine.split(" ");
                        if (parts.length >= 2 && parts[0].equals("GET") && parts[1].equals("/")) {
                            // Serve index.html
                            File file = new File("./resources/index.html");
                            if (file.exists()) {
                                try (FileInputStream fis = new FileInputStream(file)) {
                                    byte[] fileData = new byte[(int) file.length()];
                                    fis.read(fileData);

                                    output.println("HTTP/1.1 200 OK");
                                    output.println("Content-Type: text/html");
                                    output.println("Content-Length: " + fileData.length);

                                    output.println(); // Blank line to separate headers from body
                                    output.flush();

                                    clientSocket.getOutputStream().write(fileData);
                                }
                            } else {
                                output.println("HTTP/1.1 404 Not Found");
                                output.println();
                            }
                        }
                    }
                }
            }
        }
    }
}