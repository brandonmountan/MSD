import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.FileInputStream;

public class CryptoUtils {
    public static Certificate loadCertificate(String certFile) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(certFile)) {
            return cf.generateCertificate(fis);
        }
    }

    public static PrivateKey loadPrivateKey(String keyFile) throws Exception {
        byte[] keyBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(keyFile));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}