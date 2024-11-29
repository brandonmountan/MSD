import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * ChatRoom manages a group of clients and facilitates broadcasting messages
 * between them. It ensures thread-safe access to the client list.
 */
public class ChatRoom {
    private final Set<PrintWriter> clients = new HashSet<>();

    /**
     * Adds a client to the chat room.
     *
     * @param out the PrintWriter of the client to be added
     */
    public synchronized void addClient(PrintWriter out) {
        clients.add(out);
    }

    /**
     * Removes a client from the chat room.
     *
     * @param out the PrintWriter of the client to be removed
     */
    public synchronized void removeClient(PrintWriter out) {
        clients.remove(out);
    }

    /**
     * Broadcasts a message to all clients in the chat room.
     *
     * @param message the message to be sent to all clients
     */
    public synchronized void broadcast(String message) {
        // Prepare the WebSocket frame
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int payloadLength = messageBytes.length;

        // WebSocket frame header
        byte[] frame = new byte[2 + payloadLength]; // 2 bytes for header + payload length
        frame[0] = (byte) 0b10000001; // FIN + text frame opcode (0x1)

        // Set the payload length
        if (payloadLength <= 125) {
            frame[1] = (byte) payloadLength; // 0x7F bits
        } else if (payloadLength <= 65535) {
            frame[1] = 126; // Indicates extended payload length
            frame[2] = (byte) ((payloadLength >> 8) & 0xFF); // High byte
            frame[3] = (byte) (payloadLength & 0xFF); // Low byte
        } else {
            // Handle payload length too long if needed
            throw new IllegalArgumentException("Payload length too long");
        }

        // Copy the message into the frame
        System.arraycopy(messageBytes, 0, frame, 2 + (payloadLength > 125 ? 2 : 0), payloadLength);

        // Send the frame to each client
        for (PrintWriter client : clients) {
            client.write(new String(frame, StandardCharsets.ISO_8859_1)); // Use ISO_8859_1 to send raw bytes
            client.flush();
        }
    }

}
