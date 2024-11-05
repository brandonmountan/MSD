import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;


    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HTTPRequest httpRequest = new HTTPRequest(inputStream);


            String upgradeKey = httpRequest.getHeader("Sec-WebSocket-Key");

            if (httpRequest.isWebSocketUpgrade() && upgradeKey != null) {
                HTTPResponse httpResponse = new HTTPResponse(outputStream);
                httpResponse.sendWebSocketHandshake(upgradeKey);
            } else {
                HTTPResponse httpResponse = new HTTPResponse(outputStream);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

//    private void handleHttpRequest(HTTPRequest request) throws IOException {
//        System.out.println("made it to handleHttpRequest");
//        HTTPResponse response = new HTTPResponse(socket.getOutputStream());
//        File file = new File("src/" + request.getPath());
//        if (file.exists() && !file.isDirectory()) {
//            sendFile(response, file);
//        } else {
//            response.send404();
//        }
//    }

//    private void handleWebSocket(Scanner scanner, String upgradeKey) throws IOException, NoSuchAlgorithmException {
//        System.out.println("made it to handleWebSocket");
//
//        // Create the accept key
//        String acceptKey = createWebSocketAcceptKey(upgradeKey);
//        out.println("HTTP/1.1 101 Switching Protocols");
//        out.println("Upgrade: websocket");
//        out.println("Connection: Upgrade");
//        out.println("Sec-WebSocket-Accept: " + acceptKey);
//        out.println();
//
//        // Join the chat room
//        ChatRoom chatRoom = rooms.computeIfAbsent(roomName, ChatRoom::new);
//        chatRoom.addClient(out);
//        chatRoom.broadcast("A new user has joined the room: " + roomName);
//
//        // Handle WebSocket messages
//        handleWebSocketMessages(scanner, chatRoom);
//
//        // Clean up on disconnect
//        chatRoom.removeClient(out);
//        chatRoom.broadcast("A user has left the room: " + roomName);
//    }
//
//    private String createWebSocketAcceptKey(String key) throws IOException, NoSuchAlgorithmException {
//        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//        String acceptKey = key + GUID;
//        MessageDigest digest = MessageDigest.getInstance("SHA-1");
//        byte[] hashed = digest.digest(acceptKey.getBytes(StandardCharsets.UTF_8));
//        return Base64.getEncoder().encodeToString(hashed);
//    }
//
//    private void handleWebSocketMessages(Scanner scanner, ChatRoom chatRoom) throws IOException {
//        InputStream inputStream = socket.getInputStream();
//        while (true) {
//            byte[] frameHeader = new byte[2];
//            if (inputStream.read(frameHeader) == -1) break; // Break on end of stream
//
//            // Extract opcode and payload length from frame header
//            boolean isFinalFrame = (frameHeader[0] & 0b10000000) != 0;
//            int opcode = frameHeader[0] & 0b00001111; // Get opcode from the first 4 bits
//            int payloadLength = frameHeader[1] & 0b01111111; // Get payload length
//
//            // Handle the payload length (for simplicity, we handle only small payloads here)
//            if (payloadLength == 126) {
//                // If payload length is 126, the actual length follows in the next 2 bytes
//                byte[] lengthBytes = new byte[2];
//                inputStream.read(lengthBytes);
//                payloadLength = ((lengthBytes[0] & 0xFF) << 8) | (lengthBytes[1] & 0xFF);
//            } else if (payloadLength == 127) {
//                // For payload length of 127, the length follows in the next 8 bytes (not handling for simplicity)
//                throw new UnsupportedOperationException("Payload length 127 not supported");
//            }
//
//            // Read the mask key if the mask bit is set
//            byte[] maskKey = new byte[4];
//            if ((frameHeader[1] & 0b10000000) != 0) {
//                inputStream.read(maskKey);
//            }
//
//            // Read the payload data
//            byte[] payloadData = new byte[payloadLength];
//            inputStream.read(payloadData);
//
//            // Unmask the payload data if it was masked
//            if (maskKey != null) {
//                for (int i = 0; i < payloadLength; i++) {
//                    payloadData[i] ^= maskKey[i % 4]; // Unmasking
//                }
//            }
//
//            // Convert payload data to string
//            String message = new String(payloadData, StandardCharsets.UTF_8);
//            chatRoom.broadcast(message); // Broadcast the received message to all clients in the room
//        }
//    }
//
//
//    private void sendFile(HTTPResponse response, File file) throws IOException {
//        response.sendFile(file);
//        try (FileInputStream fileInputStream = new FileInputStream(file);
//             OutputStream socketOut = socket.getOutputStream()) {
//
//            int fileSize = (int) file.length();
//            for (int i = 0; i < fileSize; i++) {
//                socketOut.write(fileInputStream.read());
//                socketOut.flush();
//                Thread.sleep(10); // Artificial delay to simulate slow server
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); // Restore interrupted status
//        }
//    }
}
