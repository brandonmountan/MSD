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

public class WebSocket {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(80);
        try {
            System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for connections…");
            while (true) {
                Socket client = server.accept();
                System.out.println("A client connected.");
                new Thread(new ClientHandler(client)).start(); // Start a new thread for each client
            }
        } finally {
            server.close();
        }
    }

    static class ClientHandler implements Runnable {
        Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() { // executed by thread to handle client
            try {
                handleClient(client);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        private void handleClient(Socket client) throws IOException, NoSuchAlgorithmException {
            InputStream in = client.getInputStream(); // used to read data from the client
            OutputStream out = client.getOutputStream(); // used to send data back to the client
            Scanner scanner = new Scanner(in, "UTF-8"); // simplifies reading text from the input stream

            // Read the handshake request
            String data = scanner.useDelimiter("\\r\\n\\r\\n").next(); // reads the handshake request until it encounters two consecutive newlines
            System.out.println("received handshake data:\n" + data);

            Matcher get = Pattern.compile("^GET").matcher(data); // checks if the GET request is valid and attempts to find the Sec-WebSocket-Key header in the request

            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                if (match.find()) {
                    // Prepare the handshake response
                    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Sec-WebSocket-Accept: "
                            + Base64.getEncoder().encodeToString(
                            MessageDigest.getInstance("SHA-1").digest(
                                    (match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")
                            )
                    )
                            + "\r\n\r\n").getBytes("UTF-8");

                    // Send the handshake response
                    out.write(response, 0, response.length);
                    out.flush();
                    System.out.println("Handshake completed for a client.");
                } else {
                    System.out.println("No Sec-WebSocket-Key found.");
                }
            } else {
                System.out.println("Invalid WebSocket handshake request.");
            }

            // Main loop to read and echo messages so it continuously reads messages from the client until client disconnects
            while (true) {
                byte[] buffer = new byte[1024];
                int readBytes = in.read(buffer, 0, buffer.length);
                if (readBytes == -1) break; // Client disconnected

                String message = decodeMessage(buffer, readBytes);
                System.out.println("Received message: " + message);
                String responseMessage = "Echo: " + message;

                // Send the echo response
                out.write(encodeMessage(responseMessage), 0, encodeMessage(responseMessage).length);
                out.flush();
            }

            System.out.println("Client disconnected.");
            client.close();
            scanner.close();
        }

        private String decodeMessage(byte[] buffer, int length) {
            int opcode = buffer[0] & 0x0F;
            if (opcode != 1) return ""; // Only text frames (opcode 1) supported

            int payloadLength = buffer[1] & 0x7F;
            int start = 2;

            if (payloadLength == 126) {
                payloadLength = ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
                start += 2;
            } else if (payloadLength == 127) {
                return ""; // This example ignores this case
            }

            return new String(buffer, start, payloadLength);
        }

        private byte[] encodeMessage(String message) {
            byte[] messageBytes = message.getBytes();
            int payloadLength = messageBytes.length;

            // Frame the message
            byte[] frame = new byte[payloadLength + 2];
            frame[0] = (byte) 0b10000001; // FIN + opcode for text
            frame[1] = (byte) payloadLength; // Payload length
            System.arraycopy(messageBytes, 0, frame, 2, payloadLength);

            return frame;
        }
    }
}
