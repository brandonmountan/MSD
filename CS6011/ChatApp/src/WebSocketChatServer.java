import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class WebSocketChatServer {
    public static final int PORT = 8080;
    private static final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Initialize default chat rooms
        chatRooms.put("general", new ChatRoom("general"));
        chatRooms.put("random", new ChatRoom("random"));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("WebSocket Chat Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    static class ChatRoom {
        private final String name;
        private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

        public ChatRoom(String name) {
            this.name = name;
        }

        public synchronized void addClient(ClientHandler client) {
            clients.add(client);
            broadcast(client.getUsername() + " has joined the room.");
        }

        public synchronized void removeClient(ClientHandler client) {
            clients.remove(client);
            broadcast(client.getUsername() + " has left the room.");
        }

        public synchronized void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String username;
        private ChatRoom currentRoom;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Perform WebSocket handshake
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    System.out.println(line);
                    if (line.toLowerCase().startsWith("Sec-WebSocket-Key=")) {
                        String key = line.substring(19).trim();
                        String acceptKey = Base64.getEncoder().encodeToString(
                                MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes()));

                        out.println("HTTP/1.1 101 Switching Protocols");
                        out.println("Upgrade: websocket");
                        out.println("Connection: Upgrade");
                        out.println("Sec-WebSocket-Accept: " + acceptKey);
                        out.println();
                        out.flush();
                        break;
                    }
                }

                // Get username
                out.println("Enter your username:");
                username = in.readLine();
                out.println("Welcome, " + username + "! Available rooms: " + chatRooms.keySet());
                out.println("Enter the room you want to join:");

                String roomName = in.readLine();
                currentRoom = chatRooms.getOrDefault(roomName, chatRooms.get("general"));
                currentRoom.addClient(this);

                // Main chat loop
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/leave")) {
                        currentRoom.removeClient(this);
                        out.println("You have left the room.");
                        break;
                    } else if (message.startsWith("/join ")) {
                        String newRoomName = message.substring(6);
                        ChatRoom newRoom = chatRooms.getOrDefault(newRoomName, chatRooms.get("general"));
                        currentRoom.removeClient(this);
                        newRoom.addClient(this);
                        currentRoom = newRoom;
                    } else {
                        currentRoom.broadcast(username + ": " + message);
                    }
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    if (currentRoom != null) {
                        currentRoom.removeClient(this);
                    }
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

        public String getUsername() {
            return username;
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
