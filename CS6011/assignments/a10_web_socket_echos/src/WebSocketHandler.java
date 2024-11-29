import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocketHandler implements Runnable {
    private Socket clientSocket;

    public WebSocketHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {
            // Perform the WebSocket handshake
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String secWebSocketKey = null;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Sec-WebSocket-Key:")) {
                    secWebSocketKey = line.split(": ")[1].trim();
                }
            }

            if (secWebSocketKey != null) {
                String secWebSocketAccept = Base64.getEncoder().encodeToString(
                        MessageDigest.getInstance("SHA-1")
                                .digest((secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes())
                );

                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println("HTTP/1.1 101 Switching Protocols");
                writer.println("Upgrade: websocket");
                writer.println("Connection: Upgrade");
                writer.println("Sec-WebSocket-Accept: " + secWebSocketAccept);
                writer.println(); // End of headers

                // Begin listening for and responding to WebSocket messages
                handleWebSocketMessages(inputStream, outputStream);
            }
        } catch (Exception e) {
            System.err.println("WebSocket handshake or message handling failed: " + e.getMessage());
        }
    }

    private void handleWebSocketMessages(InputStream inputStream, OutputStream outputStream) {
        try {
            // WebSocket message reading and sending logic
            // Implement WebSocket message framing and processing here
            // For simplicity, echoing received messages
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter writer = new PrintWriter(outputStream, true);
            String message;

            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                writer.println("Echo: " + message);
            }
        } catch (IOException e) {
            System.err.println("Error while handling WebSocket messages: " + e.getMessage());
        }
    }
}
