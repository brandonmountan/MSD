import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a chat room that manages clients and messages.
 */
public class Room {
    private static final HashMap<String, Room> rooms = new HashMap<>();
    private final String name;
    private final List<Socket> clients;

    /**
     * Private constructor to prevent direct instantiation. Use getRoom to access or create rooms.
     *
     * @param name the name of the room
     */
    private Room(String name) {
        this.name = name;
        this.clients = new ArrayList<>();
    }

    /**
     * Factory method to get or create a room by name.
     * Ensures thread-safe access and creation of rooms.
     *
     * @param name the name of the room
     * @return the existing or newly created Room
     */
    public static synchronized Room getRoom(String name) {
        return rooms.computeIfAbsent(name, Room::new);
    }

    /**
     * Adds a client (Socket) to the room.
     *
     * @param client the client to add
     */
    public synchronized void addClient(Socket client) {
        clients.add(client);
    }

    /**
     * Removes a client (Socket) from the room.
     *
     * @param client the client to remove
     */
    public synchronized void removeClient(Socket client) {
        clients.remove(client);
    }

    /**
     * Broadcasts a message to all clients in the room.
     *
     * @param message the message to send
     */
    public synchronized void sendMessageToAll(String message) {
        for (Socket client : clients) {
            try {
                client.getOutputStream().write((message + "\n").getBytes("UTF-8"));
                client.getOutputStream().flush();
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the name of the room.
     *
     * @return the room name
     */
    public String getName() {
        return name;
    }
}
