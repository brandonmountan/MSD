import java.io.*;

public class HTTPResponse {
    private PrintWriter out;
    private OutputStream socketOut;

    public HTTPResponse(PrintWriter out, OutputStream socketOut) {
        this.out = out;
        this.socketOut = socketOut; // Store the OutputStream
    }

    public void sendFileResponse(File file) throws IOException, InterruptedException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println(); // Blank line between headers and content

        try (InputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                socketOut.write(buffer, 0, bytesRead);
                socketOut.flush();
                // Artificially slow down the response
                Thread.sleep(10); // Adjust the sleep time as needed
            }
        }
    }

    public void sendErrorResponse(int statusCode, String message) {
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println(); // Blank line
        out.println("<h1>" + statusCode + " " + message + "</h1>");
        out.flush(); // Ensure all data is sent
    }
}
