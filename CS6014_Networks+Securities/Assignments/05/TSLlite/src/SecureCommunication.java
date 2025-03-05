import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.Socket;
import java.security.*;

public class SecureCommunication {
    private Socket socket;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private Mac mac;

    public SecureCommunication(Socket socket, SecretKeySpec encryptKey, IvParameterSpec iv) throws Exception {
        this.socket = socket;

        // Initialize encryption cipher
        encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, encryptKey, iv);

        // Initialize decryption cipher
        decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, encryptKey, iv);

        // Initialize MAC
        mac = Mac.getInstance("HmacSHA256");
        mac.init(encryptKey);
    }

    public void sendMessage(byte[] message) throws Exception {
        // Compute HMAC
        byte[] hmac = mac.doFinal(message);

        // Combine message and HMAC
        byte[] combined = new byte[message.length + hmac.length];
        System.arraycopy(message, 0, combined, 0, message.length);
        System.arraycopy(hmac, 0, combined, message.length, hmac.length);

        // Encrypt combined data
        byte[] encrypted = encryptCipher.doFinal(combined);

        // Send encrypted data
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(encrypted);
    }

    public byte[] receiveMessage() throws Exception {
        // Receive encrypted data
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        byte[] encrypted = (byte[]) in.readObject();

        // Decrypt data
        byte[] decrypted = decryptCipher.doFinal(encrypted);

        // Split message and HMAC
        byte[] message = new byte[decrypted.length - mac.getMacLength()];
        byte[] receivedHmac = new byte[mac.getMacLength()];
        System.arraycopy(decrypted, 0, message, 0, message.length);
        System.arraycopy(decrypted, message.length, receivedHmac, 0, receivedHmac.length);

        // Verify HMAC
        byte[] computedHmac = mac.doFinal(message);
        if (!MessageDigest.isEqual(receivedHmac, computedHmac)) {
            throw new SecurityException("HMAC verification failed");
        }

        return message;
    }
}