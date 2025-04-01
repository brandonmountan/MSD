import javax.crypto.Mac;                  // For HMAC (Hash-based Message Authentication Code)
import javax.crypto.SecretKey;            // Represents a secret (symmetric) key
import javax.crypto.spec.IvParameterSpec; // For Initialization Vectors (IVs) in encryption
import javax.crypto.spec.SecretKeySpec;   // Wraps raw bytes into a SecretKey object

public class HKDF {

    /**
     * HKDF-Expand step: Derives a key from a pseudorandom key (PRK) and a tag.
     * @param input The pseudorandom key (PRK)
     * @param tag A label for key derivation (e.g., "server encrypt")
     * @return First 16 bytes of the derived key
     */
    public static byte[] hkdfExpand(byte[] input, String tag) throws Exception {
        // Initialize HMAC-SHA256 with the input key
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(input, "HmacSHA256"));

        // Prepare data: tag + byte(1) (e.g., "server encrypt" + 0x01)
        byte[] data = (tag + (char)1).getBytes();

        // Compute HMAC to derive the output key material (OKM)
        byte[] okm = hmac.doFinal(data);

        // Extract the first 16 bytes as the result
        byte[] result = new byte[16];
        System.arraycopy(okm, 0, result, 0, 16);
        return result;
    }

    /**
     * Generates a set of cryptographic keys for secure communication.
     * @param clientNonce A random value from the client (used as HMAC key)
     * @param sharedSecret The shared secret (e.g., from Diffie-Hellman)
     * @return A SecretKeys object containing encryption keys, MAC keys, and IVs
     */
    public static SecretKeys makeSecretKeys(byte[] clientNonce, byte[] sharedSecret) throws Exception {
        // Step 1: Extract pseudorandom key (PRK) using HMAC-SHA256
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(clientNonce, "HmacSHA256"));
        byte[] prk = hmac.doFinal(sharedSecret);

        // Step 2: Expand PRK into multiple keys in a chain
        byte[] serverEncrypt = hkdfExpand(prk, "server encrypt");
        byte[] clientEncrypt = hkdfExpand(serverEncrypt, "client encrypt");
        byte[] serverMAC = hkdfExpand(clientEncrypt, "server MAC");
        byte[] clientMAC = hkdfExpand(serverMAC, "client MAC");
        byte[] serverIV = hkdfExpand(clientMAC, "server IV");
        byte[] clientIV = hkdfExpand(serverIV, "client IV");

        // Wrap keys and IVs into a SecretKeys object
        return new SecretKeys(
                new SecretKeySpec(serverEncrypt, "AES"),          // Server encryption key
                new SecretKeySpec(clientEncrypt, "AES"),          // Client encryption key
                new SecretKeySpec(serverMAC, "HmacSHA256"),       // Server MAC key
                new SecretKeySpec(clientMAC, "HmacSHA256"),        // Client MAC key
                new IvParameterSpec(serverIV),                     // Server IV
                new IvParameterSpec(clientIV)                      // Client IV
        );
    }

    /**
     * Container class to hold all derived keys and IVs.
     */
    public static class SecretKeys {
        public final SecretKey serverEncrypt;  // AES key for server-to-client encryption
        public final SecretKey clientEncrypt;  // AES key for client-to-server encryption
        public final SecretKey serverMAC;     // HMAC key for server messages
        public final SecretKey clientMAC;     // HMAC key for client messages
        public final IvParameterSpec serverIV; // Initialization Vector for server AES
        public final IvParameterSpec clientIV; // Initialization Vector for client AES

        public SecretKeys(SecretKey serverEncrypt, SecretKey clientEncrypt,
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