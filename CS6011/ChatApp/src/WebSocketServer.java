import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketServer {

    public static void main(String[] args) {
        int port = 8080; // Port to run the server
        System.out.println("Starting WebSocket server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("New client connected");

                    InputStream input = clientSocket.getInputStream();
                    OutputStream output = clientSocket.getOutputStream();
                    Scanner scanner = new Scanner(input, "UTF-8");

                    // Handshake
                    String request = scanner.useDelimiter("\\r\\n\\r\\n").next();
                    System.out.println(request);
                    Matcher getMatcher = Pattern.compile("^GET").matcher(request);

                    if (getMatcher.find()) {
                        performHandshake(request, output);
                        handleClientMessages(input, output);
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static void performHandshake(String request, OutputStream output) throws IOException, NoSuchAlgorithmException {
        Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(request);
        if (match.find()) {
            String key = match.group(1).trim();
            String acceptKey = Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-1")
                            .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Sec-WebSocket-Accept: " + acceptKey + "\r\n\r\n";
            output.write(response.getBytes("UTF-8"));
            System.out.println("Handshake successful");
        }
    }

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
                payloadLength = 0; // Simplification: we do not handle large payloads in this example
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

    private static void sendMessage(OutputStream output, String message) throws IOException {
        byte[] messageBytes = message.getBytes("UTF-8");
        int payloadLength = messageBytes.length;

        // Constructing header
        byte[] header = new byte[2];
        header[0] = (byte) 0b10000001; // FIN + Text frame

        if (payloadLength <= 125) {
            header[1] = (byte) payloadLength;
            output.write(header);
        } else if (payloadLength <= 65535) {
            header[1] = 126;
            output.write(header);
            output.write((payloadLength >>> 8) & 0xFF);
            output.write(payloadLength & 0xFF);
        } else {
            header[1] = 127;
            output.write(header);
            output.write(new byte[8]); // Simplification: we do not handle large payloads
        }

        // Writing the payload
        output.write(messageBytes);
    }
}
