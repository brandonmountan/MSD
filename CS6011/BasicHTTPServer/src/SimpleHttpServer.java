import java.io.*;
import java.net.*;

public class SimpleHttpServer {
    private static final int PORT = 8080;
    private static final String RESOURCE_FOLDER = "resources"; // Folder containing your files

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String requestLine = reader.readLine();
            if (requestLine != null) {
                System.out.println("Request: " + requestLine);
                String[] tokens = requestLine.split(" ");
                String method = tokens[0];
                String path = tokens[1];

                if ("GET".equalsIgnoreCase(method)) {
                    serveFile(path, socket);
                } else {
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println("HTTP/1.1 501 Not Implemented");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void serveFile(String path, Socket socket) {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        File file = new File(RESOURCE_FOLDER + path);
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            if (file.exists() && !file.isDirectory()) {
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: text/html");
                writer.println("Content-Length: " + file.length());
                writer.println(); // Blank line after headers

                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.transferTo(outputStream);
                }
            } else {
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-Type: text/html");
                writer.println();
                writer.println("<html><body><h1>404 Not Found</h1></body></html>");
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("HTTP/1.1 500 Internal Server Error");
                writer.println("Content-Type: text/html");
                writer.println();
                writer.println("<html><body><h1>500 Internal Server Error</h1></body></html>");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
