import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private final Set<PrintWriter> clients = new HashSet<>();

    public synchronized void addClient(PrintWriter out) {
        clients.add(out);
    }

    public synchronized void removeClient(PrintWriter out) {
        clients.remove(out);
    }

    public synchronized void broadcast(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }
}
