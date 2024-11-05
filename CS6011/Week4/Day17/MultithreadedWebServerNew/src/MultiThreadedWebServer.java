import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedWebServer {
    private static final int PORT = 8080;
    private static final String RESOURCE_DIR = "resources/";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClientRequest(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            HttpRequest request = new HttpRequest(inputStream);
            File file = new File(RESOURCE_DIR + request.getRequestedFile());
            HttpResponse response = new HttpResponse(outputStream);

            if (file.exists() && !file.isDirectory()) {
                response.sendResponse(file);
            } else {
                response.sendNotFound();
            }
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
            try {
                new HttpResponse(clientSocket.getOutputStream()).sendError("An error occurred while processing your request.");
            } catch (IOException ioException) {
                System.err.println("Failed to send error response: " + ioException.getMessage());
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
