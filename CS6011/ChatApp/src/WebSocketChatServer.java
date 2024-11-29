import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketChatServer {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("WebSocket server started on port 8080.");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket client;
        private PrintWriter out;
        private String username;
        private String roomName;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                handleClient(client);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleClient(Socket client) throws IOException, NoSuchAlgorithmException {
            InputStream in = client.getInputStream();
            out = new PrintWriter(client.getOutputStream(), true);

            // WebSocket handshake
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String requestLine = reader.readLine();
            String secWebSocketKey = null;

            while (true) {
                String line = reader.readLine();
                if (line.isEmpty()) break;
                if (line.startsWith("Sec-WebSocket-Key: ")) {
                    secWebSocketKey = line.substring(19);
                    System.out.println(secWebSocketKey);
                }
            }

            // Send handshake response
            String acceptKey = Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1").digest(
                            (secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")
                    )
            );
            System.out.println(acceptKey);

            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
            out.println(response);

            // Handle messages
            while (true) {
                byte[] buffer = new byte[1024];
                int readBytes = in.read(buffer);
                if (readBytes == -1) break; // Client disconnected

                String message = decodeMessage(buffer, readBytes);
                System.out.println("Received message: " + message);
                handleMessage(message);
            }
        }

        private void handleMessage(String message) {
            String[] parts = message.split(" ");
            if (parts.length < 2) return;

            switch (parts[0]) {
                case "join":
                    if (parts.length < 3) return;
                    joinRoom(parts[1], parts[2]);
                    break;
                case "leave":
                    leaveRoom();
                    break;
                case "message":
                    sendMessage(message.substring(parts[0].length() + 1));
                    break;
            }
        }

        private void joinRoom(String username, String roomName) {
            this.username = username;
            this.roomName = roomName;

            Room room = rooms.computeIfAbsent(roomName, Room::new);
            room.addClient(this);
            broadcastMessage(username + " has joined the room.");
        }

        private void leaveRoom() {
            if (roomName != null) {
                Room room = rooms.get(roomName);
                if (room != null) {
                    room.removeClient(this);
                    broadcastMessage(username + " has left the room.");
                }
            }
        }

        private void sendMessage(String message) {
            System.out.println(message);
            broadcastMessage(username + ": " + message);
        }

        private void broadcastMessage(String message) {
            if (roomName != null) {
                Room room = rooms.get(roomName);
                if (room != null) {
                    room.broadcast(message);
                }
            }
        }

        private String decodeMessage(byte[] buffer, int length) {
            int opcode = buffer[0] & 0x0F;
            if (opcode != 1) return ""; // Only text frames (opcode 1) supported

            int payloadLength = buffer[1] & 0x7F;
            int start = 2;

            return new String(buffer, start, payloadLength);
        }
    }

    static class Room {
        private final String name;
        private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

        public Room(String name) {
            this.name = name;
        }

        public synchronized void addClient(ClientHandler client) {
            clients.add(client);
        }

        public synchronized void removeClient(ClientHandler client) {
            clients.remove(client);
        }

        public synchronized void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(encodeMessage(message));
            }
        }

        private byte[] encodeMessage(String message) {
            byte[] messageBytes = message.getBytes();
            int payloadLength = messageBytes.length;

            byte[] frame = new byte[payloadLength + 2];
            frame[0] = (byte) 0b10000001; // FIN + opcode for text
            frame[1] = (byte) payloadLength; // Payload length
            System.arraycopy(messageBytes, 0, frame, 2, payloadLength);

            return frame;
        }
    }
}
