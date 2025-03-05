import javax.crypto.spec.*;

public class SessionKeys {
    private SecretKeySpec serverEncryptKey;
    private SecretKeySpec clientEncryptKey;
    private SecretKeySpec serverMACKey;
    private SecretKeySpec clientMACKey;
    private IvParameterSpec serverIV;
    private IvParameterSpec clientIV;

    public SessionKeys(SecretKeySpec serverEncryptKey, SecretKeySpec clientEncryptKey,
                       SecretKeySpec serverMACKey, SecretKeySpec clientMACKey,
                       IvParameterSpec serverIV, IvParameterSpec clientIV) {
        this.serverEncryptKey = serverEncryptKey;
        this.clientEncryptKey = clientEncryptKey;
        this.serverMACKey = serverMACKey;
        this.clientMACKey = clientMACKey;
        this.serverIV = serverIV;
        this.clientIV = clientIV;
    }

    public SecretKeySpec getServerEncryptKey() { return serverEncryptKey; }
    public SecretKeySpec getClientEncryptKey() { return clientEncryptKey; }
    public SecretKeySpec getServerMACKey() { return serverMACKey; }
    public SecretKeySpec getClientMACKey() { return clientMACKey; }
    public IvParameterSpec getServerIV() { return serverIV; }
    public IvParameterSpec getClientIV() { return clientIV; }
}