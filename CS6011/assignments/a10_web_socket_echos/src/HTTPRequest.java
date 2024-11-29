import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HTTPRequest {
    private String method;
    private String path;
    private HashMap<String, String> headers = new HashMap<>();

    public HTTPRequest(InputStream inputStream) throws IOException {
        try (Scanner scanner = new Scanner(inputStream)) {
            parseRequestLine(scanner);
            parseHeaders(scanner);
        }
    }

    private void parseRequestLine(Scanner scanner) {
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            method = parts[0];
            path = parts[1];
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
            System.out.println(headers);
        }
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }
}
