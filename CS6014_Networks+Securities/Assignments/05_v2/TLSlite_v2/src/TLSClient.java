import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.nio.file.*;
import java.security.cert.Certificate;

public class TLSClient {
    public static void main(String[] args) {
        try {
            // Load certificates and keys from DER files
            Certificate caCert = CertificateManager.loadCertificate("CAcertificate.der");
            Certificate clientCert = CertificateManager.loadCertificate("CASignedClientCertificate.der");
            PrivateKey clientPrivateKey = CertificateManager.loadPrivateKey("clientPrivateKey.der");

            // Connect to server
            try (Socket socket = new Socket("localhost", 12345)) {
                System.out.println("Connected to server");

                // Perform handshake
                KeyGenerator.SessionKeys keys = TLSHandshake.clientHandshake(
                        socket, caCert, clientCert, clientPrivateKey);

                // Set up secure communication
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Receive server messages
                for (int i = 0; i < 2; i++) {
                    byte[] encrypted = (byte[])in.readObject();
                    byte[] decrypted = SecureMessage.decryptAndVerify(
                            encrypted, keys.serverEncrypt, keys.serverMAC, keys.serverIV);
                    System.out.println("Server says: " + new String(decrypted));
                }

                // Send response
                String response = "Hello server! I received your messages.";
                byte[] encryptedResponse = SecureMessage.encryptAndMac(
                        response.getBytes(), keys.clientEncrypt, keys.clientMAC, keys.clientIV);
                out.writeObject(encryptedResponse);
                System.out.println("Client sent response");
            }
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}