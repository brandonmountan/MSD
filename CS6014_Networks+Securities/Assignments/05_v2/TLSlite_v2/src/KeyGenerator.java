import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.math.BigInteger;

public class KeyGenerator {
    private static final int KEY_LENGTH = 16; // 16 bytes = 128 bits

    public static SecretKey deriveKey(byte[] inputKeyMaterial, String tag) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(inputKeyMaterial, "HmacSHA256");
        hmac.init(keySpec);

        byte[] info = tag.getBytes();
        byte[] data = new byte[info.length + 1];
        System.arraycopy(info, 0, data, 0, info.length);
        data[info.length] = 1; // Single byte with value 1

        byte[] okm = hmac.doFinal(data);
        byte[] truncated = new byte[KEY_LENGTH];
        System.arraycopy(okm, 0, truncated, 0, KEY_LENGTH);

        return new SecretKeySpec(truncated, "AES");
    }

    public static SessionKeys makeSecretKeys(byte[] clientNonce, BigInteger sharedSecret) throws Exception {
        // Convert BigInteger to byte array properly
        byte[] sharedSecretBytes = sharedSecret.toByteArray();

        // First extract PRK
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(clientNonce, "HmacSHA256");
        hmac.init(keySpec);
        byte[] prk = hmac.doFinal(sharedSecretBytes);

        // Now expand keys
        SecretKey serverEncrypt = deriveKey(prk, "server encrypt");
        SecretKey clientEncrypt = deriveKey(serverEncrypt.getEncoded(), "client encrypt");
        SecretKey serverMAC = deriveKey(clientEncrypt.getEncoded(), "server MAC");
        SecretKey clientMAC = deriveKey(serverMAC.getEncoded(), "client MAC");

        // For IVs, we'll use the same derivation but create IvParameterSpec objects
        SecretKey serverIVKey = deriveKey(clientMAC.getEncoded(), "server IV");
        SecretKey clientIVKey = deriveKey(serverIVKey.getEncoded(), "client IV");

        // Create IV parameters from the first 16 bytes of the derived keys
        IvParameterSpec serverIV = new IvParameterSpec(serverIVKey.getEncoded());
        IvParameterSpec clientIV = new IvParameterSpec(clientIVKey.getEncoded());

        return new SessionKeys(serverEncrypt, clientEncrypt, serverMAC, clientMAC,
                serverIV, clientIV);
    }

    public static class SessionKeys {
        public final SecretKey serverEncrypt;
        public final SecretKey clientEncrypt;
        public final SecretKey serverMAC;
        public final SecretKey clientMAC;
        public final IvParameterSpec serverIV;
        public final IvParameterSpec clientIV;

        public SessionKeys(SecretKey serverEncrypt, SecretKey clientEncrypt,
                           SecretKey serverMAC, SecretKey clientMAC,
                           IvParameterSpec serverIV, IvParameterSpec clientIV) {
            this.serverEncrypt = serverEncrypt;
            this.clientEncrypt = clientEncrypt;
            this.serverMAC = serverMAC;
            this.clientMAC = clientMAC;
            this.serverIV = serverIV;
            this.clientIV = clientIV;
        }
    }
}