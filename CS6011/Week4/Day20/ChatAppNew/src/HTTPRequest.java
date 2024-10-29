import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();

    public HTTPRequest(BufferedReader reader) throws IOException {
        parseRequest(reader);
    }

    private void parseRequest(BufferedReader reader) throws IOException {
        // Read the request line
        String requestLine = reader.readLine();
        if (requestLine != null) {
            String[] requestParts = requestLine.split(" ");
            method = requestParts[0];
            path = requestParts[1];
        }

        // Read headers
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ");
            headers.put(headerParts[0], headerParts[1]);
        }
    }

    public boolean isWebSocketUpgrade() {
        return "GET".equalsIgnoreCase(method) &&
                "websocket".equalsIgnoreCase(headers.get("Upgrade")) &&
                headers.containsKey("Connection") &&
                headers.get("Connection").toLowerCase().contains("upgrade");
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    // New method to retrieve headers
    public Map<String, String> getHeaders() {
        return headers;
    }
}
