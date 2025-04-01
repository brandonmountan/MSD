import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import javax.crypto.*;

public class TLSHandshake {
    private static final int NONCE_LENGTH = 32;

    // Client side handshake
    public static KeyGenerator.SessionKeys clientHandshake(
            Socket socket, Certificate caCert,
            Certificate clientCert, PrivateKey clientPrivateKey) throws Exception {

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Step 1: Client sends nonce
        SecureRandom random = new SecureRandom();
        byte[] nonce = new byte[NONCE_LENGTH];
        random.nextBytes(nonce);
        out.writeObject(nonce);

        // Step 2: Receive server certificate, DH public key, and signature
        Certificate serverCert = (Certificate)in.readObject();
        BigInteger serverDHPub = (BigInteger)in.readObject();
        byte[] serverSignature = (byte[])in.readObject();

        // Verify server certificate against CA
        serverCert.verify(caCert.getPublicKey());

        // Verify server's DH public key signature
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(serverCert.getPublicKey());
        sig.update(serverDHPub.toByteArray());
        if (!sig.verify(serverSignature)) {
            throw new SecurityException("Server signature verification failed");
        }

        // Step 3: Generate DH key pair
        BigInteger clientDHPriv = new BigInteger(2048, random);
        BigInteger clientDHPub = DHParams.g.modPow(clientDHPriv, DHParams.N);

        // Send client certificate, DH public key, and signature
        out.writeObject(clientCert);
        out.writeObject(clientDHPub);

        sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(clientPrivateKey);
        sig.update(clientDHPub.toByteArray());
        byte[] clientSignature = sig.sign();
        out.writeObject(clientSignature);

        // Compute shared secret
        BigInteger sharedSecret = serverDHPub.modPow(clientDHPriv, DHParams.N);

        // Generate session keys
        KeyGenerator.SessionKeys keys = KeyGenerator.makeSecretKeys(nonce, sharedSecret);

        // Step 4: Server sends HMAC of all handshake messages
        byte[] serverHMAC = (byte[])in.readObject();

        // Step 5: Client sends HMAC of all handshake messages
        // (Implementation would track all messages and compute HMAC)

        return keys;
    }

    // Server side handshake
    public static KeyGenerator.SessionKeys serverHandshake(
            Socket socket, Certificate caCert,
            Certificate serverCert, PrivateKey serverPrivateKey) throws Exception {

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Step 1: Receive client nonce
        byte[] nonce = (byte[])in.readObject();

        // Step 2: Generate DH key pair
        SecureRandom random = new SecureRandom();
        BigInteger serverDHPriv = new BigInteger(2048, random);
        BigInteger serverDHPub = DHParams.g.modPow(serverDHPriv, DHParams.N);

        // Sign DH public key
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(serverPrivateKey);
        sig.update(serverDHPub.toByteArray());
        byte[] serverSignature = sig.sign();

        // Send server certificate, DH public key, and signature
        out.writeObject(serverCert);
        out.writeObject(serverDHPub);
        out.writeObject(serverSignature);

        // Step 3: Receive client certificate, DH public key, and signature
        Certificate clientCert = (Certificate)in.readObject();
        BigInteger clientDHPub = (BigInteger)in.readObject();
        byte[] clientSignature = (byte[])in.readObject();

        // Verify client certificate against CA
        clientCert.verify(caCert.getPublicKey());

        // Verify client's DH public key signature
        sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(clientCert.getPublicKey());
        sig.update(clientDHPub.toByteArray());
        if (!sig.verify(clientSignature)) {
            throw new SecurityException("Client signature verification failed");
        }

        // Compute shared secret
        BigInteger sharedSecret = clientDHPub.modPow(serverDHPriv, DHParams.N);

        // Generate session keys
        KeyGenerator.SessionKeys keys = KeyGenerator.makeSecretKeys(nonce, sharedSecret);

        // Step 4: Server sends HMAC of all handshake messages
        // (Implementation would track all messages and compute HMAC)
        out.writeObject(new byte[32]); // Placeholder for HMAC

        // Step 5: Receive client HMAC
        byte[] clientHMAC = (byte[])in.readObject();

        return keys;
    }
}