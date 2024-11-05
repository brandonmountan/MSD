import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            HTTPRequest request = new HTTPRequest(scanner);
            if (request.isWebSocket()) {
                handleWebSocket(scanner, out);
            } else {
                handleHttpRequest(request, out);
            }

        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleHttpRequest(HTTPRequest request, PrintWriter out) throws IOException {
        HTTPResponse response = new HTTPResponse(out, socket.getOutputStream());
        File file = new File("resources" + request.getFileRequested());

        if (file.exists() && !file.isDirectory()) {
            sendFile(response, file);
        } else {
            response.send404();
        }
    }

    private void handleWebSocket(Scanner scanner, PrintWriter out) throws IOException {
        String key = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("Sec-WebSocket-Key: ")) {
                key = line.substring(19).trim();
            }
            if (line.isEmpty()) {
                break; // End of headers
            }
        }

        // Create the accept key
        String acceptKey = createWebSocketAcceptKey(key);
        out.println("HTTP/1.1 101 Switching Protocols");
        out.println("Upgrade: websocket");
        out.println("Connection: Upgrade");
        out.println("Sec-WebSocket-Accept: " + acceptKey);
        out.println();

        // Handle WebSocket messages
        handleWebSocketMessages(scanner, out);
    }

    private String createWebSocketAcceptKey(String key) throws IOException, NoSuchAlgorithmException {
        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        String acceptKey = key + GUID;
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashed = digest.digest(acceptKey.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }

    private void handleWebSocketMessages(Scanner scanner, PrintWriter out) {
        // For simplicity, echo back any messages
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            out.println("Received: " + message); // Echo the message back
        }
    }

    private void sendFile(HTTPResponse response, File file) throws IOException {
        response.sendFile(file);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream socketOut = socket.getOutputStream()) {

            int fileSize = (int) file.length();
            for (int i = 0; i < fileSize; i++) {
                socketOut.write(fileInputStream.read());
                socketOut.flush();
                Thread.sleep(10); // Artificial delay to simulate slow server
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }
}
