import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicHttpServer {
    public static void main(String[] args) {
        // try with resources
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is listening on port 8080");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            String requestLine = readRequestLine(inputStream);
            System.out.println("Request: " + requestLine);
            String[] tokens = requestLine.split(" ");

            if (tokens.length < 2) {
                return;
            }

            String method = tokens[0];
            String requestedFile = tokens[1];

            // Default to index.html if the root is requested
            if (requestedFile.equals("/")) {
                requestedFile = "index.html";
            }

            File file = new File("resources/" + requestedFile);
            if (file.exists() && !file.isDirectory()) {
                sendResponse(outputStream, file);
            } else {
                sendNotFound(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String readRequestLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[1024];

        // Read bytes until a newline is found
        while ((read = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
            // Check for end of the request line
            if (buffer.toString().contains("\r\n")) {
                break;
            }
        }
        return buffer.toString().split("\r\n")[0]; // Return the first line
    }

    private static void sendResponse(OutputStream os, File file) throws IOException {
        PrintWriter out = new PrintWriter(os, true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + file.length());
        out.println(); // End of headers

        // Use FileInputStream to read the file contents
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(os);
            os.flush(); // Ensure all data is sent
        }
    }

    private static void sendNotFound(OutputStream os) throws IOException {
        PrintWriter out = new PrintWriter(os, true);
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<html><body><h1>404 Not Found</h1></body></html>");
    }
}
