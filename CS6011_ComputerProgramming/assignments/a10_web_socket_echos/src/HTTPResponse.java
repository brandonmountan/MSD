import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HTTPResponse {
    private OutputStream outputStream;

    public HTTPResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendFile(File file) throws IOException {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println(); // End of headers

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                Thread.sleep(10); // Delay to simulate slower response
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }
    }

    public void send404() throws IOException {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>404 Not Found</h1></body></html>");
        out.flush();
    }

    public void sendWebSocketHandshake(String secWebSocketKey) throws IOException, NoSuchAlgorithmException {
        PrintWriter out = new PrintWriter(outputStream, true);
        String responseKey = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1")
                        .digest((secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes())
        );
        out.println("HTTP/1.1 101 Switching Protocols");
        out.println("Upgrade: websocket");
        out.println("Connection: Upgrade");
        out.println("Sec-WebSocket-Accept: " + responseKey);
        out.println();
        out.flush();
    }
}
