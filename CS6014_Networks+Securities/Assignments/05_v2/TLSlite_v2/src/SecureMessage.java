import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;

public class SecureMessage {
    public static byte[] encryptAndMac(byte[] message, SecretKey encryptKey,
                                       SecretKey macKey, IvParameterSpec iv) throws Exception {
        // Compute HMAC
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(macKey);
        byte[] mac = hmac.doFinal(message);

        // Combine message and MAC
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(message);
        baos.write(mac);
        byte[] payload = baos.toByteArray();

        // Encrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, encryptKey, iv);
        return cipher.doFinal(payload);
    }

    public static byte[] decryptAndVerify(byte[] ciphertext, SecretKey encryptKey,
                                          SecretKey macKey, IvParameterSpec iv) throws Exception {
        // Decrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, encryptKey, iv);
        byte[] decrypted = cipher.doFinal(ciphertext);

        // Split message and MAC (HMAC-SHA256 produces 32-byte output)
        if (decrypted.length < 32) throw new SecurityException("Invalid message");
        int messageLength = decrypted.length - 32;
        byte[] message = new byte[messageLength];
        byte[] receivedMac = new byte[32];
        System.arraycopy(decrypted, 0, message, 0, messageLength);
        System.arraycopy(decrypted, messageLength, receivedMac, 0, 32);

        // Verify MAC
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(macKey);
        byte[] computedMac = hmac.doFinal(message);

        if (!MessageDigest.isEqual(computedMac, receivedMac)) {
            throw new SecurityException("MAC verification failed");
        }

        return message;
    }
}