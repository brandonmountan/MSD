import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class RequestHandler implements Runnable {
    private Socket clientSocket;

    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            HTTPRequest httpRequest = new HTTPRequest(inputStream);

            HashMap<String, String> headers = httpRequest.getHeaders();

            if (headers.containsKey("Sec-WebSocket-Key")) {
                System.out.println("WebSocket upgrade request detected.");
                // Handle WebSocket handshake and upgrade
                handleWebSocketUpgrade(inputStream, clientSocket.getOutputStream());
            } else {
                System.out.println("Regular HTTP request detected.");
                // Handle HTTP request as before
                System.out.println("Request path: " + httpRequest.getPath());
                File file = new File("websocket_index.html");
                HTTPResponse response = new HTTPResponse(clientSocket.getOutputStream());

                if (file.exists() && !file.isDirectory()) {
                    System.out.println("File found, sending response.");
                    response.sendFile(file);
                } else {
                    System.out.println("File not found, sending 404 response.");
                    response.send404();
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
            try {
                new HTTPResponse(clientSocket.getOutputStream()).send404();
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

    private void handleWebSocketUpgrade(InputStream inputStream, OutputStream outputStream) throws IOException {
        HTTPRequest httpRequest = new HTTPRequest(inputStream);
        String secWebSocketKey = httpRequest.getHeaders().get("Sec-WebSocket-Key");

        if (secWebSocketKey != null) {
            System.out.println("Sec-WebSocket-Key header found: " + secWebSocketKey);
            HTTPResponse response = new HTTPResponse(outputStream);
            try {
                response.sendWebSocketHandshake(secWebSocketKey);
                System.out.println("WebSocket handshake completed successfully.");
            } catch (Exception e) {
                System.err.println("Error during WebSocket handshake: " + e.getMessage());
            }
        } else {
            System.err.println("Missing Sec-WebSocket-Key header for WebSocket handshake.");
        }
    }
}
