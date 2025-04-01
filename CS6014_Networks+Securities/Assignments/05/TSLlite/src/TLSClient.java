import java.io.*;          // For input/output operations
import java.net.*;         // For network connections
import java.security.*;    // For security functions
import java.security.cert.Certificate;  // For handling digital certificates

public class TLSClient {
    public static void main(String[] args) throws Exception {
        // Check if user provided all required arguments
        if (args.length != 4) {
            System.out.println("Usage: TLSClient <host> <port> <clientCertFile> <clientPrivateKeyFile>");
            return;  // Exit if arguments are missing
        }

        // Store command line arguments in easy-to-understand variables
        String host = args[0];                 // Server address (e.g., "example.com")
        int port = Integer.parseInt(args[1]);  // Server port number (e.g., 443)
        String clientCertFile = args[2];       // Client's certificate file
        String clientPrivateKeyFile = args[3]; // Client's private key file

        // Load the Certificate Authority (CA) certificate that we trust
        Certificate caCert = CryptoUtils.loadCertificate("CAcertificate.pem");

        // Load the client's own certificate and private key
        Certificate clientCert = CryptoUtils.loadCertificate(clientCertFile);
        PrivateKey clientPrivateKey = CryptoUtils.loadPrivateKey(clientPrivateKeyFile);

        // Try to connect to the server (auto-closes when done)
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to server: " + socket.getInetAddress());

            // Perform the TLS handshake (like introducing ourselves securely)
            TLSHandshake.HandshakeResult result = TLSHandshake.clientHandshake(
                    socket, clientCert, clientPrivateKey, caCert);

            // After handshake, create a secure communication channel
            SecureChannel channel = result.clientChannel;

            // Receive two messages from the server
            byte[] msg1 = channel.receiveMessage();
            byte[] msg2 = channel.receiveMessage();

            // Print the received messages
            System.out.println("Received from server:");
            System.out.println(new String(msg1));  // Convert bytes to text
            System.out.println(new String(msg2));

            // Send a response back to the server
            channel.sendMessage("Hello back from client!".getBytes());

        } catch (Exception e) {
            // If anything goes wrong, show the error
            System.err.println("Error communicating with server: " + e.getMessage());
            e.printStackTrace();  // Detailed error information
        }
    }
}