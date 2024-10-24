import java.io.*;

public class HTTPResponse {
    private OutputStream outputStream;

    public HTTPResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(File file) throws IOException {
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
