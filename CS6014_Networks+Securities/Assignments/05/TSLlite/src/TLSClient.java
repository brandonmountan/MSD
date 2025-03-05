import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.Certificate;

public class TLSClient {
    public static void main(String[] args) {
        try {
            // Load client's certificate and private key
            Certificate clientCert = CryptoUtils.loadCertificate("CASignedClientCertificate.pem");
            PrivateKey clientPrivateKey = CryptoUtils.loadPrivateKey("clientPrivateKey.der");

            // Load CA's certificate
            Certificate caCert = CryptoUtils.loadCertificate("CAcertificate.pem");

            // Connect to server
            Socket serverSocket = new Socket("localhost", 12345);
            System.out.println("Connected to server!");

            // Perform handshake
            HandshakeHandler handshake = new HandshakeHandler(serverSocket, clientCert, clientPrivateKey, caCert);
            handshake.performClientHandshake();

            // Set up secure communication
            SecureCommunication secureComm = new SecureCommunication(
                    serverSocket,
                    handshake.getSessionKeys().getClientEncryptKey(),
                    handshake.getSessionKeys().getClientIV()
            );

            // Receive messages
            byte[] message1 = secureComm.receiveMessage();
            System.out.println("Received from server: " + new String(message1));

            byte[] message2 = secureComm.receiveMessage();
            System.out.println("Received from server: " + new String(message2));

            // Send response
            secureComm.sendMessage("Hello from client!".getBytes());

            // Close connection
            serverSocket.close();
            System.out.println("Connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}