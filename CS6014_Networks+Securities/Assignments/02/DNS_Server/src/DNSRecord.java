import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

public class DNSRecord {
    private String[] name;
    private int type;
    private int clazz;
    private long ttl;
    private int rdLength;
    private byte[] rdata;
    private Date creationDate;

    // Private constructor to enforce immutability
    private DNSRecord(String[] name, int type, int clazz, long ttl, int rdLength, byte[] rdata) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.rdLength = rdLength;
        this.rdata = rdata;
        this.creationDate = new Date();
    }

    // Decode a DNS record from an input stream
    public static DNSRecord decodeRecord(InputStream in, DNSMessage message) throws IOException {
        String[] name = message.readDomainName(in);
        int type = readTwoBytes(in);
        int clazz = readTwoBytes(in);
        long ttl = readFourBytes(in);
        int rdLength = readTwoBytes(in);
        byte[] rdata = new byte[rdLength];
        in.read(rdata);
        return new DNSRecord(name, type, clazz, ttl, rdLength, rdata);
    }

    // Write the record to an output stream
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        DNSMessage.writeDomainName(out, domainNameLocations, name);
        writeTwoBytes(out, type);
        writeTwoBytes(out, clazz);
        writeFourBytes(out, ttl);
        writeTwoBytes(out, rdLength);
        out.write(rdata);
    }

    // Check if the record is expired
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = creationDate.getTime() + (ttl * 1000);
        return currentTime > expirationTime;
    }

    // Helper method to read 2 bytes from an input stream
    private static int readTwoBytes(InputStream in) throws IOException {
        return (in.read() << 8) | in.read();
    }

    // Helper method to read 4 bytes from an input stream
    private static long readFourBytes(InputStream in) throws IOException {
        return ((long) in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }

    // Helper method to write 2 bytes to an output stream
    private static void writeTwoBytes(ByteArrayOutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    // Helper method to write 4 bytes to an output stream
    private static void writeFourBytes(ByteArrayOutputStream out, long value) throws IOException {
        out.write((int) ((value >> 24) & 0xFF));
        out.write((int) ((value >> 16) & 0xFF));
        out.write((int) ((value >> 8) & 0xFF));
        out.write((int) (value & 0xFF));
    }

    @Override
    public String toString() {
        return "DNSRecord{" +
                "name=" + String.join(".", name) +
                ", type=" + type +
                ", clazz=" + clazz +
                ", ttl=" + ttl +
                ", rdLength=" + rdLength +
                ", creationDate=" + creationDate +
                '}';
    }

    // Getters
    public String[] getName() { return name; }
    public int getType() { return type; }
    public int getClazz() { return clazz; }
    public long getTtl() { return ttl; }
    public int getRdLength() { return rdLength; }
    public byte[] getRdata() { return rdata; }
    public Date getCreationDate() { return creationDate; }
}