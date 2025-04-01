// Import necessary tools
import javax.crypto.Mac;       // For message authentication codes
import java.io.*;              // For reading/writing data
import java.math.BigInteger;   // For big numbers (used in crypto)
import java.net.*;             // For network connections
import java.security.*;        // For security functions
import java.security.cert.Certificate;  // For digital certificates

public class TLSHandshake {

    // A container to store results after handshake completes
    public static class HandshakeResult {
        public final SecureChannel serverChannel;  // Secure channel for server
        public final SecureChannel clientChannel;  // Secure channel for client
        public final byte[] handshakeMessages;    // Record of all handshake steps

        public HandshakeResult(SecureChannel serverChannel,
                               SecureChannel clientChannel,
                               byte[] handshakeMessages) {
            this.serverChannel = serverChannel;
            this.clientChannel = clientChannel;
            this.handshakeMessages = handshakeMessages;
        }
    }

    // Server's steps to establish a secure connection
    public static HandshakeResult serverHandshake(Socket socket,
                                                  Certificate serverCert,
                                                  PrivateKey serverPrivateKey,
                                                  Certificate caCert) throws Exception {
        // Set up communication streams
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Record all handshake messages for verification
        ByteArrayOutputStream handshakeMessages = new ByteArrayOutputStream();

        // --- STEP 1: Client sends a random number (nonce) ---
        byte[] clientNonce = (byte[]) in.readObject();
        handshakeMessages.write(clientNonce);

        // --- STEP 2: Server responds with its identity ---
        // 2a. Generate temporary key (for key exchange)
        DHKeyExchange serverDH = new DHKeyExchange();
        byte[] serverDHPubBytes = serverDH.getPublicValue().toByteArray();

        // 2b. Sign the temporary key with server's private key
        byte[] signedServerDHPub = CryptoUtils.sign(serverPrivateKey, serverDHPubBytes);

        // 2c. Send certificate, temporary key, and signature
        out.writeObject(serverCert);
        out.writeObject(serverDHPubBytes);
        out.writeObject(signedServerDHPub);
        out.flush();

        // Record what we sent
        handshakeMessages.write(serverCert.getEncoded());
        handshakeMessages.write(serverDHPubBytes);
        handshakeMessages.write(signedServerDHPub);

        // --- STEP 3: Client sends its identity ---
        // 3a. Receive client's certificate, temporary key, and signature
        Certificate clientCert = (Certificate) in.readObject();
        byte[] clientDHPubBytes = (byte[]) in.readObject();
        byte[] signedClientDHPub = (byte[]) in.readObject();

        // Record what we received
        handshakeMessages.write(clientCert.getEncoded());
        handshakeMessages.write(clientDHPubBytes);
        handshakeMessages.write(signedClientDHPub);

        // 3b. Verify client's certificate is trusted
        if (!CryptoUtils.verifyCertificate(clientCert, caCert)) {
            throw new SecurityException("Client certificate is invalid!");
        }

        // 3c. Verify client's signature
        if (!CryptoUtils.verify(clientCert, clientDHPubBytes, signedClientDHPub)) {
            throw new SecurityException("Client's key signature is invalid!");
        }

        // --- STEP 4: Compute shared secret ---
        BigInteger clientDHPub = new BigInteger(clientDHPubBytes);
        byte[] sharedSecret = serverDH.getSharedSecret(clientDHPub).toByteArray();

        // --- STEP 5: Generate session keys ---
        HKDF.SecretKeys keys = HKDF.makeSecretKeys(clientNonce, sharedSecret);

        // --- STEP 6: Server sends verification MAC ---
        Mac serverMac = Mac.getInstance("HmacSHA256");
        serverMac.init(keys.serverMAC);
        byte[] serverHandshakeMAC = serverMac.doFinal(handshakeMessages.toByteArray());
        out.writeObject(serverHandshakeMAC);
        out.flush();
        handshakeMessages.write(serverHandshakeMAC);

        // --- STEP 7: Client sends verification MAC ---
        byte[] clientHandshakeMAC = (byte[]) in.readObject();
        handshakeMessages.write(clientHandshakeMAC);

        // Verify client's MAC
        Mac clientMac = Mac.getInstance("HmacSHA256");
        clientMac.init(keys.clientMAC);
        byte[] expectedClientMAC = clientMac.doFinal(handshakeMessages.toByteArray());
        if (!MessageDigest.isEqual(expectedClientMAC, clientHandshakeMAC)) {
            throw new SecurityException("Client's verification failed!");
        }

        // --- FINAL: Create secure channels ---
        SecureChannel serverChannel = new SecureChannel(
                keys.serverEncrypt, keys.serverMAC, keys.serverIV, out, in);

        SecureChannel clientChannel = new SecureChannel(
                keys.clientEncrypt, keys.clientMAC, keys.clientIV, out, in);

        return new HandshakeResult(serverChannel, clientChannel,
                handshakeMessages.toByteArray());
    }

    public static HandshakeResult clientHandshake(Socket socket,
                                                  Certificate clientCert,
                                                  PrivateKey clientPrivateKey,
                                                  Certificate caCert) throws Exception {
        // Set up communication streams
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Record all handshake messages for verification
        ByteArrayOutputStream handshakeMessages = new ByteArrayOutputStream();

        // --- STEP 1: Client sends random nonce first ---
        SecureRandom random = new SecureRandom();
        byte[] clientNonce = new byte[32];
        random.nextBytes(clientNonce);
        out.writeObject(clientNonce);
        out.flush();
        handshakeMessages.write(clientNonce);

        // --- STEP 2: Receive server's certificate and DH public value ---
        Certificate serverCert = (Certificate) in.readObject();
        byte[] serverDHPubBytes = (byte[]) in.readObject();
        byte[] signedServerDHPub = (byte[]) in.readObject();

        handshakeMessages.write(serverCert.getEncoded());
        handshakeMessages.write(serverDHPubBytes);
        handshakeMessages.write(signedServerDHPub);

        // Verify server certificate
        if (!CryptoUtils.verifyCertificate(serverCert, caCert)) {
            throw new SecurityException("Server certificate verification failed");
        }

        // Verify server's signed DH public value
        if (!CryptoUtils.verify(serverCert, serverDHPubBytes, signedServerDHPub)) {
            throw new SecurityException("Server DH public value signature verification failed");
        }

        // --- STEP 3: Client sends its certificate and DH public value ---
        DHKeyExchange clientDH = new DHKeyExchange();
        byte[] clientDHPubBytes = clientDH.getPublicValue().toByteArray();
        byte[] signedClientDHPub = CryptoUtils.sign(clientPrivateKey, clientDHPubBytes);

        out.writeObject(clientCert);
        out.writeObject(clientDHPubBytes);
        out.writeObject(signedClientDHPub);
        out.flush();

        handshakeMessages.write(clientCert.getEncoded());
        handshakeMessages.write(clientDHPubBytes);
        handshakeMessages.write(signedClientDHPub);

        // --- STEP 4: Compute shared secret ---
        BigInteger serverDHPub = new BigInteger(serverDHPubBytes);
        byte[] sharedSecret = clientDH.getSharedSecret(serverDHPub).toByteArray();

        // --- STEP 5: Generate session keys ---
        HKDF.SecretKeys keys = HKDF.makeSecretKeys(clientNonce, sharedSecret);

        // --- STEP 6: Receive server's handshake MAC ---
        byte[] serverHandshakeMAC = (byte[]) in.readObject();
        handshakeMessages.write(serverHandshakeMAC);

        // Verify server's MAC
        Mac serverMac = Mac.getInstance("HmacSHA256");
        serverMac.init(keys.serverMAC);
        byte[] expectedServerMAC = serverMac.doFinal(handshakeMessages.toByteArray());
        if (!MessageDigest.isEqual(expectedServerMAC, serverHandshakeMAC)) {
            throw new SecurityException("Server handshake MAC verification failed");
        }

        // --- STEP 7: Client sends its handshake MAC ---
        Mac clientMac = Mac.getInstance("HmacSHA256");
        clientMac.init(keys.clientMAC);
        byte[] clientHandshakeMAC = clientMac.doFinal(handshakeMessages.toByteArray());
        out.writeObject(clientHandshakeMAC);
        out.flush();

        // --- FINAL: Create secure channels ---
        SecureChannel serverChannel = new SecureChannel(
                keys.serverEncrypt, keys.serverMAC, keys.serverIV, out, in);

        SecureChannel clientChannel = new SecureChannel(
                keys.clientEncrypt, keys.clientMAC, keys.clientIV, out, in);

        return new HandshakeResult(serverChannel, clientChannel,
                handshakeMessages.toByteArray());
    }
}