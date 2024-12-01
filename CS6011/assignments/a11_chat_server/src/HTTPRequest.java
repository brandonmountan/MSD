import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class HTTPRequest {
    private String method;
    private HashMap<String, String> headers = new HashMap<>();
    private String requestedFile;

    public HTTPRequest(InputStream inputStream) throws IOException {
        try {
            Scanner scanner = new Scanner(inputStream);
            parseRequestLine(scanner);
            parseHeaders(scanner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void parseRequestLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            method = tokens[0];
            System.out.println("method: " + method);
            requestedFile = tokens[1].equals("/") ? "websocket_index.html" : "index.html";
            System.out.println("requestedFile: " + requestedFile);
        }
    }

    private void parseHeaders(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break; // End of headers
            }
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getRequestedFile() {
        return requestedFile;
    }
}
