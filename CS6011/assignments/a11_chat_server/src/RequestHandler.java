import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Handles client requests, including both regular HTTP requests and WebSocket upgrades.
 */
public class RequestHandler implements Runnable {
    private Socket clientSocket;

    /**
     * Constructs a RequestHandler to manage the specified client socket.
     *
     * @param socket the client socket
     */
    public RequestHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * Processes client requests by either serving HTTP files or upgrading to WebSocket communication.
     */
    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            HTTPRequest request = new HTTPRequest(inputStream);

            File file = new File(Main.RESOURCE_DIR + request.getRequestedFile());
            HashMap<String, String> headers = request.getHeaders();

            OutputStream outputStream = clientSocket.getOutputStream();

            if (headers.containsKey("Sec-WebSocket-Key")) {
                System.out.println("WebSocket upgrade detected");
                String secWebSocketKey = headers.get("Sec-WebSocket-Key");
                HTTPResponse response = new HTTPResponse(outputStream);
                response.sendWebSocketHandshake(secWebSocketKey);

                while (true) {
                    handleClientMessages(inputStream, outputStream);
                }
            } else {
                System.out.println("Regular HTTP request detected");
                HTTPResponse response = new HTTPResponse(outputStream);
                if (file.exists() && !file.isDirectory()) {
                    response.sendFile(file);
                } else {
                    response.send404();
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles WebSocket client messages, including decoding and echoing messages back.
     *
     * @param input  the input stream from the client
     * @param output the output stream to the client
     * @throws IOException if an I/O error occurs
     */
    private static void handleClientMessages(InputStream input, OutputStream output) throws IOException {
        while (true) {
            // Reading the first two bytes
            byte[] header = new byte[2];
            input.read(header);

            boolean fin = (header[0] & 0b10000000) != 0;
            int opcode = header[0] & 0b00001111;

            if (opcode == 0x8) { // Close frame
                System.out.println("Client disconnected");
                break;
            }

            int payloadLength = header[1] & 0b01111111;
            if (payloadLength == 126) {
                byte[] extended = new byte[2];
                input.read(extended);
                payloadLength = ((extended[0] & 0xFF) << 8) | (extended[1] & 0xFF);
            } else if (payloadLength == 127) {
                byte[] extended = new byte[8];
                input.read(extended);
                payloadLength = (int) ((extended[0] & 0xFFL) << 56
                        | (extended[1] & 0xFFL) << 48
                        | (extended[2] & 0xFFL) << 40
                        | (extended[3] & 0xFFL) << 32
                        | (extended[4] & 0xFFL) << 24
                        | (extended[5] & 0xFFL) << 16
                        | (extended[6] & 0xFFL) << 8
                        | (extended[7] & 0xFFL));

                if (payloadLength > Integer.MAX_VALUE) {
                    throw new IOException("Payload size exceeds supported limits.");
                }
            }

            // Masking key
            byte[] maskingKey = new byte[4];
            input.read(maskingKey);

            // Decoding the payload
            byte[] encodedPayload = new byte[payloadLength];
            input.read(encodedPayload);
            byte[] decodedPayload = new byte[payloadLength];

            for (int i = 0; i < payloadLength; i++) {
                decodedPayload[i] = (byte) (encodedPayload[i] ^ maskingKey[i % 4]);
            }

            String message = new String(decodedPayload, "UTF-8");
            System.out.println("Received: " + message);

            // Echo the message back to the client
            sendMessage(output, message);
        }
    }

    /**
     * Sends a message to the WebSocket client.
     *
     * @param output  the output stream to the client
     * @param message the message to send
     * @throws IOException if an I/O error occurs
     */
    private static void sendMessage(OutputStream output, String message) throws IOException {
        byte[] messageBytes = message.getBytes("UTF-8");
        int payloadLength = messageBytes.length;

        // Constructing header
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
        headerStream.write(0b10000001); // FIN + Text frame

        if (payloadLength <= 125) {
            headerStream.write(payloadLength);
        } else if (payloadLength <= 65535) {
            headerStream.write(126);
            headerStream.write((payloadLength >>> 8) & 0xFF);
            headerStream.write(payloadLength & 0xFF);
        } else {
            headerStream.write(127);
            headerStream.write(new byte[] {
                    (byte) ((payloadLength >>> 56) & 0xFF),
                    (byte) ((payloadLength >>> 48) & 0xFF),
                    (byte) ((payloadLength >>> 40) & 0xFF),
                    (byte) ((payloadLength >>> 32) & 0xFF),
                    (byte) ((payloadLength >>> 24) & 0xFF),
                    (byte) ((payloadLength >>> 16) & 0xFF),
                    (byte) ((payloadLength >>> 8) & 0xFF),
                    (byte) (payloadLength & 0xFF)
            });
        }

        // Writing the header and payload
        output.write(headerStream.toByteArray());
        output.write(messageBytes);
    }
}