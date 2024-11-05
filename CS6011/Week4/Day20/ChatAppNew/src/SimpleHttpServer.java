import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SimpleHttpServer is a multithreaded HTTP server that supports WebSocket
 * connections for a real-time chat application. It listens for incoming
 * connections and delegates handling to RequestHandler instances.
 */
public class SimpleHttpServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        ChatRoom chatRoom = new ChatRoom(); // Shared chat room instance
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new RequestHandler(clientSocket, chatRoom)).start();
                } catch (IOException e) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server could not start: " + e.getMessage());
            System.exit(1);
        }
    }
}
