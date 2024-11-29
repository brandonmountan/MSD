import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * HTTPResponse facilitates sending HTTP responses back to the client.
 * It can send file responses and error responses.
 */
public class HTTPResponse {
    private final PrintWriter out;

    public HTTPResponse(PrintWriter out) {
        this.out = out;
    }

    /**
     * Sends a file response for the requested file.
     *
     * @param file the file to be sent
     * @throws IOException if an I/O error occurs
     */
    public void sendFileResponse(File file) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println();
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(new String(buffer, 0, bytesRead));
        }
        fis.close();
    }

    /**
     * Sends an error response to the client.
     *
     * @param statusCode the HTTP status code
     * @param message    the error message to be sent
     */
    public void sendErrorResponse(int statusCode, String message) {
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/plain");
        out.println();
        out.println(message);
    }
}
