import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Scanner;

public class MyHttpServer {
    private static final int PORT = 8080;
    private static final String RESOURCE_FOLDER = "resources";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
             Scanner scanner = new Scanner(input)) {

            // Read the request line
            if (scanner.hasNextLine()) {
                String requestLine = scanner.nextLine();
                System.out.println("Request: " + requestLine);

                // Parse the request
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String path = tokens[1];

                // Default to index.html for root requests
                if (path.equals("/")) {
                    path = "/resources/index.html";
                }

                // Serve the file
                serveFile(path, output, clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Client handling exception: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    private static void serveFile(String path, PrintWriter output, Socket clientSocket) {
        File file = new File(RESOURCE_FOLDER + path);
        if (file.exists() && !file.isDirectory()) {
            try {
                // Set content type based on file extension
                String contentType = Files.probeContentType(file.toPath());
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: " + contentType);
                output.println("Content-Length: " + file.length());
                output.println(); // Blank line separating headers from content

                // Send the file content using BufferedOutputStream
                try (InputStream fileInput = new FileInputStream(file);
                     OutputStream outputStream = clientSocket.getOutputStream();
                     BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream)) {
                    fileInput.transferTo(bufferedOutput);
                    bufferedOutput.flush(); // Ensure all data is sent
                }
            } catch (IOException e) {
                output.println("HTTP/1.1 500 Internal Server Error");
                output.println();
                output.println("500 Internal Server Error");
            }
        } else {
            output.println("HTTP/1.1 404 Not Found");
            output.println();
            output.println("404 Not Found");
        }
    }
}
