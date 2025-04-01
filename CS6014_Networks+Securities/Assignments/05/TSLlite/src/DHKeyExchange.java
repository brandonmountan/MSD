import java.math.BigInteger;
import java.security.SecureRandom;

public class DHKeyExchange {

    // Predefined constants for Diffie-Hellman:
    // g = generator (base), N = large prime modulus (2048-bit)
    // These values are from RFC 3526 (a standard for DH groups)
    private static final BigInteger g = new BigInteger("2");  // Generator (small prime, commonly 2)
    private static final BigInteger N = new BigInteger(
            "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1" +
                    "29024E088A67CC74020BBEA63B139B22514A08798E3404DD" +
                    "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245" +
                    "E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7ED" +
                    "EE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3D" +
                    "C2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F" +
                    "83655D23DCA3AD961C62F356208552BB9ED529077096966D" +
                    "670C354E4ABC9804F1746C08CA18217C32905E462E36CE3B" +
                    "E39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9" +
                    "DE2BCBF6955817183995497CEA956AE515D2261898FA0510" +
                    "15728E5A8AAAC42DAD33170D04507A33A85521ABDF1CBA64" +
                    "ECFB850458DBEF0A8AEA71575D060C7DB3970F85A6E1E4C7" +
                    "ABF5AE8CDB0933D71E8C94E04A25619DCEE3D2261AD2EE6B" +
                    "F12FFA06D98A0864D87602733EC86A64521F2B18177B200C" +
                    "BBE117577A615D6C770988C0BAD946E208E24FA074E5AB31" +
                    "43DB5BFCE0FD108E4B82D120A93AD2CAFFFFFFFFFFFFFFFF", 16);  // '16' means interpret as hexadecimal

    // Instance variables:
    private BigInteger privateValue;  // Secret key (randomly generated)
    private BigInteger publicValue;   // Public key (computed as g^privateValue mod N)

    // Constructor: Generates a new DH key pair
    public DHKeyExchange() {
        SecureRandom random = new SecureRandom();  // Cryptographically secure random number generator
        privateValue = new BigInteger(2048, random);  // Generate 2048-bit random private key
        publicValue = g.modPow(privateValue, N);     // Compute public key: g^privateValue mod N
    }

    // Getter method to share the public key with others
    public BigInteger getPublicValue() {
        return publicValue;  // Returns the computed public key
    }

    // Computes the shared secret using the other party's public key
    public BigInteger getSharedSecret(BigInteger otherPublicValue) {
        return otherPublicValue.modPow(privateValue, N);  // Computes: (otherPublic^privateValue) mod N
    }
}