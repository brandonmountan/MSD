import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            HTTPRequest request = new HTTPRequest(inputStream);
            File file = new File(Main.RESOURCE_DIR + request.getRequestedFile());
            System.out.println(file);
            HTTPResponse response = new HTTPResponse(outputStream);

            if (file.exists() && !file.isDirectory()) {
                response.sendResponse(file);
            } else {
                response.sendNotFound();
            }
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
            try {
                new HTTPResponse(clientSocket.getOutputStream()).sendError("An error occurred while processing your request.");
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
