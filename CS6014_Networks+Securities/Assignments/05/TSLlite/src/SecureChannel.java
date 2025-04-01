import javax.crypto.*;                  // For cryptographic operations (Cipher, Mac)
import javax.crypto.spec.*;             // For cryptographic parameter specs (SecretKeySpec, IvParameterSpec)
import java.io.*;                       // For input/output streams
import java.security.*;                 // For security utilities (MessageDigest)

public class SecureChannel {
    // Encryption key for AES
    private final SecretKey encryptKey;
    // MAC key for HMAC-SHA256 (ensures message integrity)
    private final SecretKey macKey;
    // Initialization Vector (IV) for AES-CBC mode
    private final IvParameterSpec iv;
    // Cipher object for AES encryption/decryption
    private final Cipher cipher;
    // MAC object for HMAC computation
    private final Mac mac;
    // Output stream to send data
    private final ObjectOutputStream out;
    // Input stream to receive data
    private final ObjectInputStream in;

    public SecureChannel(SecretKey encryptKey, SecretKey macKey, IvParameterSpec iv,
                         ObjectOutputStream out, ObjectInputStream in) throws Exception {
        // Initialize keys and IV
        this.encryptKey = encryptKey;
        this.macKey = macKey;
        this.iv = iv;
        // Initialize I/O streams
        this.out = out;
        this.in = in;

        // Set up AES-CBC cipher with PKCS#5 padding
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Set up HMAC-SHA256 for message authentication
        this.mac = Mac.getInstance("HmacSHA256");
        this.mac.init(macKey); // Initialize MAC with the provided key
    }

    public void sendMessage(byte[] message) throws Exception {
        // Compute HMAC of the message (for integrity)
        mac.reset(); // Reset MAC state
        byte[] messageMAC = mac.doFinal(message); // Generate HMAC-SHA256

        // Combine message and MAC into a single byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(message);      // Write the original message
        bos.write(messageMAC);   // Append the MAC
        byte[] payload = bos.toByteArray();

        // Encrypt the payload (message + MAC) using AES-CBC
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey, iv);
        byte[] encrypted = cipher.doFinal(payload);

        // Send the encrypted data over the output stream
        out.writeObject(encrypted);
        out.flush(); // Ensure data is sent immediately
    }

    public byte[] receiveMessage() throws Exception {
        // Read encrypted data from the input stream
        byte[] encrypted = (byte[]) in.readObject();

        // Decrypt the data using AES-CBC
        cipher.init(Cipher.DECRYPT_MODE, encryptKey, iv);
        byte[] decrypted = cipher.doFinal(encrypted);

        // Split decrypted data into message and MAC
        byte[] message = new byte[decrypted.length - 32]; // HMAC-SHA256 is 32 bytes
        byte[] receivedMAC = new byte[32];
        System.arraycopy(decrypted, 0, message, 0, message.length); // Extract message
        System.arraycopy(decrypted, message.length, receivedMAC, 0, 32); // Extract MAC

        // Verify the MAC to ensure message integrity
        mac.reset();
        byte[] computedMAC = mac.doFinal(message);
        if (!MessageDigest.isEqual(computedMAC, receivedMAC)) {
            throw new SecurityException("MAC verification failed"); // Detect tampering
        }

        return message; // Return the verified message
    }
}