import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HTTPRequest {
    private String method;
    private String requestedFile;

    public HTTPRequest(InputStream inputStream) throws IOException {
        parseRequest(inputStream);
    }

    private void parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        if (requestLine != null) {
            String[] tokens = requestLine.split(" ");
            if (tokens.length >= 2) {
                this.method = tokens[0];
                this.requestedFile = tokens[1].equals("/") ? "index.html" : tokens[1];
            } else {
                throw new IOException("Malformed request");
            }
        } else {
            throw new IOException("Empty request");
        }
    }

    public String getRequestedFile() {
        return requestedFile;
    }
}
