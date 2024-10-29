import java.io.*;
import java.net.*;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private ChatRoom chatRoom;

    public RequestHandler(Socket socket, ChatRoom chatRoom) {
        this.clientSocket = socket;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            HTTPResponse response = new HTTPResponse(out, clientSocket.getOutputStream());
            HTTPRequest request = new HTTPRequest(in);

            // Check if the requested path is "/"
            if ("/".equals(request.getPath())) {
                File indexFile = new File("./resources/index.html");
                if (indexFile.exists() && indexFile.isFile()) {
                    response.sendFileResponse(indexFile);
                } else {
                    response.sendErrorResponse(404, "File Not Found");
                }
            } else if ("GET".equalsIgnoreCase(request.getMethod())) {
                // Handle other GET requests here (if needed)
                // For example, serve other static files
                File requestedFile = new File("resources" + request.getPath());
                if (requestedFile.exists() && requestedFile.isFile()) {
                    response.sendFileResponse(requestedFile);
                } else {
                    response.sendErrorResponse(404, "File Not Found");
                }
            } else {
                response.sendErrorResponse(501, "Not Implemented");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error processing request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
