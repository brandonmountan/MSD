import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RequestHandler implements Runnable {
    private Socket clientSocket;

    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            HTTPRequest request = new HTTPRequest(inputStream);

            File file = new File(Main.RESOURCE_DIR + request.getRequestedFile());
            System.out.println("file: " + file);
            HashMap<String, String> headers = request.getHeaders();
            System.out.println("headers: " + headers);

            OutputStream outputStream = clientSocket.getOutputStream();

            if (headers.containsKey("Sec-WebSocket-Key")) {
                System.out.println("websocket upgrade detected");
                String secWebSocketKey = headers.get("Sec-WebSocket-Key");
                System.out.println("secWebSocketKey: " + secWebSocketKey);
                HTTPResponse response = new HTTPResponse(outputStream);
                response.sendWebSocketHandshake(secWebSocketKey);
            } else {
                System.out.println("regular http request detected");
                HTTPResponse response = new HTTPResponse(outputStream);
                if (file.exists() && !file.isDirectory()) {
                    response.sendFile(file);
                    System.out.println("file found, sending response");
                } else {
                    System.out.println("file not found, sending 404");
                    response.send404();
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
