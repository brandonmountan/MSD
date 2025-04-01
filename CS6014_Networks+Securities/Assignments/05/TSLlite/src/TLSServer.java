import java.io.*;               // For reading/writing data
import java.net.*;              // For network connections
import java.security.*;         // For security functions
import java.security.cert.Certificate;  // For digital certificates

public class TLSServer {
    public static void main(String[] args) throws Exception {
        // Check if the user provided all required arguments
        if (args.length != 3) {
            System.out.println("Usage: TLSServer <port> <serverCertFile> <serverPrivateKeyFile>");
            return;  // Exit if arguments are missing
        }

        // Store command line arguments in variables
        int port = Integer.parseInt(args[0]);          // Port to listen on (e.g., 4433)
        String serverCertFile = args[1];               // Server's certificate file
        String serverPrivateKeyFile = args[2];        // Server's private key file

        // Load the Certificate Authority (CA) certificate we trust
        java.security.cert.Certificate caCert = CryptoUtils.loadCertificate("CAcertificate.pem");

        // Load the server's own certificate and private key
        Certificate serverCert = CryptoUtils.loadCertificate(serverCertFile);
        PrivateKey serverPrivateKey = CryptoUtils.loadPrivateKey(serverPrivateKeyFile);

        // Create a server socket to listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            // Keep running forever to handle multiple clients
            while (true) {
                // Wait for a client to connect
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Perform the TLS handshake to establish secure connection
                    TLSHandshake.HandshakeResult result = TLSHandshake.serverHandshake(
                            clientSocket, serverCert, serverPrivateKey, caCert);

                    // After handshake, get the secure communication channel
                    SecureChannel channel = result.serverChannel;

                    // Send two test messages to the client
                    channel.sendMessage("Hello from server!".getBytes());
                    channel.sendMessage("This is a secure message".getBytes());

                    // Wait for and read the client's response
                    byte[] response = channel.receiveMessage();
                    System.out.println("Received from client: " + new String(response));

                } catch (Exception e) {
                    // If something goes wrong with a client, print error but keep running
                    System.err.println("Error handling client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}