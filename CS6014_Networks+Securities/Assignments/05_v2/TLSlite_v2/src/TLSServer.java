import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.nio.file.*;
import java.security.cert.Certificate;

public class TLSServer {
    public static void main(String[] args) {
        try {
            // Load certificates and keys from DER files
            Certificate caCert = CertificateManager.loadCertificate("CAcertificate.der");
            Certificate serverCert = CertificateManager.loadCertificate("CASignedServerCertificate.der");
            PrivateKey serverPrivateKey = CertificateManager.loadPrivateKey("serverPrivateKey.der");

            // Start server
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Server listening on port 12345...");

                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        System.out.println("Client connected");

                        // Perform handshake
                        KeyGenerator.SessionKeys keys = TLSHandshake.serverHandshake(
                                clientSocket, caCert, serverCert, serverPrivateKey);

                        // Set up secure communication
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                        // Send some secure messages
                        for (int i = 0; i < 2; i++) {
                            String message = "Server message " + i;
                            byte[] encrypted = SecureMessage.encryptAndMac(
                                    message.getBytes(), keys.serverEncrypt, keys.serverMAC, keys.serverIV);
                            out.writeObject(encrypted);
                            System.out.println("Server sent: " + message);
                        }

                        // Receive client response
                        byte[] response = (byte[])in.readObject();
                        byte[] decrypted = SecureMessage.decryptAndVerify(
                                response, keys.clientEncrypt, keys.clientMAC, keys.clientIV);
                        System.out.println("Client says: " + new String(decrypted));
                    } catch (Exception e) {
                        System.err.println("Error with client connection: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}