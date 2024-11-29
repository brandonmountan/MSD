import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HTTPResponse {
    private OutputStream outputStream;
    private PrintWriter writer;

    public HTTPResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.writer = new PrintWriter(outputStream, true);
    }

    public void sendFile(File file) throws IOException {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        writer.println("Content-Length: " + file.length());
        writer.println();
        // Write file content to output stream
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        writer.flush();
    }

    public void send404() throws IOException {
        writer.println("HTTP/1.1 404 Not Found");
        writer.println("Content-Type: text/html");
        writer.println();
        writer.println("<html><body><h1>404 Not Found</h1></body></html>");
        writer.flush();
    }

    public void sendWebSocketHandshake(String secWebSocketKey) throws IOException, NoSuchAlgorithmException {
        String responseKey = Base64.getEncoder().encodeToString(
                java.security.MessageDigest.getInstance("SHA-1")
                        .digest((secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes())
        );
        writer.println("HTTP/1.1 101 Switching Protocols");
        writer.println("Upgrade: websocket");
        writer.println("Connection: Upgrade");
        writer.println("Sec-WebSocket-Accept: " + responseKey);
        writer.println();
        writer.flush();
    }
}
