import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.Certificate;

public class TLSServer {
    public static void main(String[] args) {
        try {
            // Load server's certificate and private key
            Certificate serverCert = CryptoUtils.loadCertificate("CASignedServerCertificate.pem");
            PrivateKey serverPrivateKey = CryptoUtils.loadPrivateKey("serverPrivateKey.der");

            // Load CA's certificate
            Certificate caCert = CryptoUtils.loadCertificate("CAcertificate.pem");

            // Start server socket
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server listening on port 12345...");

            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Perform handshake
                HandshakeHandler handshake = new HandshakeHandler(clientSocket, serverCert, serverPrivateKey, caCert);
                handshake.performServerHandshake();

                // Set up secure communication
                SecureCommunication secureComm = new SecureCommunication(
                        clientSocket,
                        handshake.getSessionKeys().getServerEncryptKey(),
                        handshake.getSessionKeys().getServerIV()
                );

                // Send messages
                secureComm.sendMessage("Hello from server!".getBytes());
                secureComm.sendMessage("This is a secure message.".getBytes());

                // Receive response
                byte[] response = secureComm.receiveMessage();
                System.out.println("Received from client: " + new String(response));

                // Close connection
                clientSocket.close();
                System.out.println("Connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}