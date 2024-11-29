import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpResponse {
    private OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(File file) throws IOException {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println(); // End of headers

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(outputStream);
            outputStream.flush();
        }
    }

    public void sendNotFound() throws IOException {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>404 Not Found</h1></body></html>");
    }

    public void sendError(String message) throws IOException {
        PrintWriter out = new PrintWriter(outputStream, true);
        out.println("HTTP/1.1 500 Internal Server Error");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>500 Internal Server Error</h1><p>" + message + "</p></body></html>");
    }
}
