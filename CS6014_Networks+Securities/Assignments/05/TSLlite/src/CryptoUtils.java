import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.*;
import javax.crypto.*;

public class CryptoUtils {

    // Method to load a digital certificate from a file
    // Takes the certificate file path as input and returns a Certificate object
    public static Certificate loadCertificate(String certFile) throws Exception {
        // Create a CertificateFactory that can handle X.509 format certificates (standard format)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Try-with-resources block to automatically close the file when done
        // Open the certificate file for reading
        try (FileInputStream fis = new FileInputStream(certFile)) {
            // Generate and return a Certificate object from the file contents
            return cf.generateCertificate(fis);
        }
    }

    // Method to load a private key from a DER format file
    // Takes the key file path as input and returns a PrivateKey object
    public static PrivateKey loadPrivateKey(String keyFile) throws Exception {
        // Read all bytes from the key file into a byte array
        byte[] keyBytes = new FileInputStream(keyFile).readAllBytes();

        // Create a key specification using PKCS#8 format (standard for private keys)
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        // Create a KeyFactory that can handle RSA keys
        KeyFactory kf = KeyFactory.getInstance("RSA");

        // Generate and return the private key from the key specification
        return kf.generatePrivate(spec);
    }

    // Method to verify if a certificate is signed by a Certificate Authority (CA)
    // Takes the certificate to verify and the CA's certificate as inputs
    // Returns true if verified, false if not
    public static boolean verifyCertificate(Certificate cert, Certificate caCert) {
        try {
            // Use the CA's public key to verify the certificate's signature
            cert.verify(caCert.getPublicKey());
            return true; // Verification succeeded
        } catch (Exception e) {
            return false; // Verification failed
        }
    }

    // Method to create a digital signature for some data using a private key
    // Takes the private key and data to sign as inputs
    // Returns the signature as a byte array
    public static byte[] sign(PrivateKey privateKey, byte[] data) throws Exception {
        // Create a Signature object that uses SHA-256 hash with RSA encryption
        Signature sig = Signature.getInstance("SHA256withRSA");

        // Initialize the Signature object for signing with the private key
        sig.initSign(privateKey);

        // Feed the data to be signed into the Signature object
        sig.update(data);

        // Generate and return the signature
        return sig.sign();
    }

    // Method to verify a digital signature using a certificate's public key
    // Takes the certificate, original data, and signature to verify as inputs
    // Returns true if signature is valid, false if not
    public static boolean verify(Certificate cert, byte[] data, byte[] signature) throws Exception {
        // Create a Signature object that uses SHA-256 hash with RSA encryption
        Signature sig = Signature.getInstance("SHA256withRSA");

        // Initialize the Signature object for verification with the certificate's public key
        sig.initVerify(cert.getPublicKey());

        // Feed the original data into the Signature object
        sig.update(data);

        // Check if the signature matches and return the result
        return sig.verify(signature);
    }
}