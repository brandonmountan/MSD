import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HTTPResponse {
    private OutputStream outputStream;

    public HTTPResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendWebSocketHandshake(String secWebSocketKey) throws Exception {
        String acceptKey = createAcceptKey(secWebSocketKey);
        PrintWriter writer = new PrintWriter(outputStream);
        writer.print("HTTP/1.1 101 Switching Protocols\r\n");
        writer.print("Upgrade: websocket\r\n");
        writer.print("Connection: Upgrade\r\n");
        writer.print("Sec-WebSocket-Accept: " + acceptKey + "\r\n");
        writer.print("\r\n");
        writer.flush();
    }

    private String createAcceptKey(String secWebSocketKey) throws Exception {
        String magicString = secWebSocketKey.trim() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hashed = md.digest(magicString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }

    public void handleHttpRequest(File file) {
        if (file.exists() && !file.isDirectory()) {
            sendFile(file);
        } else {
            send404();
        }
    }

    private void sendFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            PrintWriter writer = new PrintWriter(outputStream);
            writer.print("HTTP/1.1 200 OK\r\n");
            writer.print("Content-Type: application/octet-stream\r\n");
            writer.print("Content-Length: " + file.length() + "\r\n");
            writer.print("\r\n");
            writer.flush();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush(); // Ensure the data is sent immediately
            }
        } catch (IOException e) {
            // Handle exceptions such as file not found or read errors
            e.printStackTrace(); // Or log the error as needed
        }
    }

    private void send404() {
        PrintWriter writer = new PrintWriter(outputStream);
        writer.print("HTTP/1.1 404 Not Found\r\n\r\n");
        writer.flush();
    }

    public String handleWebSocketMessages(InputStream inputStream) throws IOException {
        byte[] frame = new byte[1024]; // Buffer for the incoming frame
        int bytesRead = inputStream.read(frame);

        if (bytesRead == -1) {
            throw new IOException("End of stream reached.");
        }

        // Decode the WebSocket frame
        boolean isFinalFragment = (frame[0] & 0x80) != 0;
        int opcode = frame[0] & 0x0F; // Get the opcode

        if (!isFinalFragment || opcode != 1) {
            throw new IOException("Only text frames are supported.");
        }

        // Handle payload length
        int payloadLength = frame[1] & 0x7F;
        int offset = 2;

        // Check for extended payload lengths
        if (payloadLength == 126) {
            payloadLength = ByteBuffer.wrap(frame, 2, 2).getShort(); // Read the next 2 bytes for payload length
            offset += 2;
        } else if (payloadLength == 127) {
            payloadLength = (int) ByteBuffer.wrap(frame, 2, 8).getLong(); // Read the next 8 bytes for payload length
            offset += 8;
        }

        // Masking key for client-to-server messages
        byte[] maskingKey = new byte[4];
        System.arraycopy(frame, offset, maskingKey, 0, 4);
        offset += 4;

        // Decode the payload
        byte[] payload = new byte[payloadLength];
        for (int i = 0; i < payloadLength; i++) {
            payload[i] = (byte) (frame[offset + i] ^ maskingKey[i % 4]); // Unmask the message
        }

        return new String(payload, StandardCharsets.UTF_8); // Return the decoded message as a string
    }
}
