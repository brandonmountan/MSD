import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * HTTPRequest represents an incoming HTTP request. It parses the request
 * line and headers to determine the method, path, and headers.
 */
public class HTTPRequest {
    private String method;
    private String fileRequested;
    private boolean isWebSocket;

    public HTTPRequest(Scanner scanner) throws IOException {
        if (scanner.hasNextLine()) {
            String requestLine = scanner.nextLine();
            String[] tokens = requestLine.split(" ");
            this.method = tokens[0];
            this.fileRequested = tokens[1].equals("/") ? "/index.html" : tokens[1];
            this.isWebSocket = requestLine.contains("Upgrade: websocket");
        } else {
            throw new IOException("Empty request line");
        }
    }

    public String getFileRequested() {
        return fileRequested;
    }

    public String getMethod() {
        return method;
    }

    public boolean isWebSocket() {
        return isWebSocket;
    }
}
