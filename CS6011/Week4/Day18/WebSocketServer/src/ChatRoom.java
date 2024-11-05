import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private final String name;
    private final Set<RequestHandler> clients;


    public ChatRoom(String name) {
        this.name = name;
        this.clients = new HashSet<>();
    }

    public synchronized void addClient(RequestHandler client) {
        clients.add(client);
        // Notify other clients about the new user
        broadcast(client.getUsername() + " has joined the room.");
    }

    public synchronized void removeClient(RequestHandler client) {
        if (clients.remove(client)) {
            // Notify other clients about the user leaving
            broadcast(client.getUsername() + " has left the room.");
        }
    }

    public synchronized void broadcast(String message) {
        for (RequestHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public String getName() {
        return name;
    }

    public synchronized Set<RequestHandler> getClients() {
        return new HashSet<>(clients); // Return a copy of the clients set
    }
}
