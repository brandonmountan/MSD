import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class HandshakeHandler {
    private static final BigInteger g = new BigInteger("2");
    private static final BigInteger N = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF", 16);

    private Socket socket;
    private Certificate cert;
    private PrivateKey privateKey;
    private Certificate caCert;
    private BigInteger dhPrivateKey;
    private BigInteger dhPublicKey;
    private byte[] sharedSecret;
    private SessionKeys sessionKeys;

    public HandshakeHandler(Socket socket, Certificate cert, PrivateKey privateKey, Certificate caCert) {
        this.socket = socket;
        this.cert = cert;
        this.privateKey = privateKey;
        this.caCert = caCert;
    }

    // Server-side handshake
    public void performServerHandshake() throws Exception {
        // Generate DH key pair
        dhPrivateKey = new BigInteger(2048, new SecureRandom());
        dhPublicKey = g.modPow(dhPrivateKey, N);

        // Send server certificate, DH public key, and signed DH public key
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(cert);
        out.writeObject(dhPublicKey);
        out.writeObject(sign(dhPublicKey.toByteArray()));

        // Receive client certificate, DH public key, and signed DH public key
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Certificate clientCert = (Certificate) in.readObject();
        BigInteger clientDHPublicKey = (BigInteger) in.readObject();
        byte[] clientSignedDHPublicKey = (byte[]) in.readObject();

        // Verify client certificate
        clientCert.verify(caCert.getPublicKey());

        // Verify client's signed DH public key
        if (!verify(clientCert.getPublicKey(), clientDHPublicKey.toByteArray(), clientSignedDHPublicKey)) {
            throw new SecurityException("Client DH public key verification failed");
        }

        // Compute shared secret
        sharedSecret = clientDHPublicKey.modPow(dhPrivateKey, N).toByteArray();

        // Generate session keys
        sessionKeys = generateSessionKeys(sharedSecret);
    }

    // Client-side handshake
    public void performClientHandshake() throws Exception {
        // Generate DH key pair
        dhPrivateKey = new BigInteger(2048, new SecureRandom());
        dhPublicKey = g.modPow(dhPrivateKey, N);

        // Receive server certificate, DH public key, and signed DH public key
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Certificate serverCert = (Certificate) in.readObject();
        BigInteger serverDHPublicKey = (BigInteger) in.readObject();
        byte[] serverSignedDHPublicKey = (byte[]) in.readObject();

        // Verify server certificate
        serverCert.verify(caCert.getPublicKey());

        // Verify server's signed DH public key
        if (!verify(serverCert.getPublicKey(), serverDHPublicKey.toByteArray(), serverSignedDHPublicKey)) {
            throw new SecurityException("Server DH public key verification failed");
        }

        // Send client certificate, DH public key, and signed DH public key
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(cert);
        out.writeObject(dhPublicKey);
        out.writeObject(sign(dhPublicKey.toByteArray()));

        // Compute shared secret
        sharedSecret = serverDHPublicKey.modPow(dhPrivateKey, N).toByteArray();

        // Generate session keys
        sessionKeys = generateSessionKeys(sharedSecret);
    }

    public SessionKeys getSessionKeys() {
        return sessionKeys;
    }

    private byte[] sign(byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    private boolean verify(PublicKey publicKey, byte[] data, byte[] signatureBytes) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    private SessionKeys generateSessionKeys(byte[] sharedSecret) throws Exception {
        // Use HKDF to derive keys
        byte[] prk = hmacSha256("tls13".getBytes(), sharedSecret);
        byte[] serverEncrypt = hkdfExpand(prk, "server encrypt");
        byte[] clientEncrypt = hkdfExpand(serverEncrypt, "client encrypt");
        byte[] serverMAC = hkdfExpand(clientEncrypt, "server MAC");
        byte[] clientMAC = hkdfExpand(serverMAC, "client MAC");
        byte[] serverIV = hkdfExpand(clientMAC, "server IV");
        byte[] clientIV = hkdfExpand(serverIV, "client IV");

        return new SessionKeys(
                new SecretKeySpec(serverEncrypt, "AES"),
                new SecretKeySpec(clientEncrypt, "AES"),
                new SecretKeySpec(serverMAC, "HmacSHA256"),
                new SecretKeySpec(clientMAC, "HmacSHA256"),
                new IvParameterSpec(serverIV),
                new IvParameterSpec(clientIV)
        );
    }

    private byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private byte[] hkdfExpand(byte[] prk, String info) throws Exception {
        byte[] infoBytes = info.getBytes();
        byte[] okm = hmacSha256(prk, concat(infoBytes, new byte[]{1}));
        byte[] result = new byte[16];
        System.arraycopy(okm, 0, result, 0, 16);
        return result;
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}