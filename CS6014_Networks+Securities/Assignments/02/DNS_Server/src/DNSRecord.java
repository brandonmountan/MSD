import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

/**
 * Represents a DNS resource record.
 * A DNS record contains information such as the domain name, record type, class, time-to-live (TTL), and resource data.
 */
public class DNSRecord {
    private String[] name;       // The domain name associated with the record
    private int type;            // The type of the record (e.g., A, AAAA, MX, etc.)
    private int clazz;           // The class of the record (typically IN for Internet)
    private long ttl;            // The time-to-live (TTL) for the record in seconds
    private int rdLength;        // The length of the resource data
    private byte[] rdata;        // The resource data (e.g., IP address for A records)
    private Date creationDate;   // The timestamp when the record was created

    /**
     * Private constructor to enforce immutability.
     *
     * @param name     The domain name associated with the record.
     * @param type     The type of the record.
     * @param clazz    The class of the record.
     * @param ttl      The time-to-live (TTL) for the record in seconds.
     * @param rdLength The length of the resource data.
     * @param rdata    The resource data.
     */
    private DNSRecord(String[] name, int type, int clazz, long ttl, int rdLength, byte[] rdata) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
        this.ttl = ttl;
        this.rdLength = rdLength;
        this.rdata = rdata;
        this.creationDate = new Date();
    }

    /**
     * Decodes a DNS record from an input stream.
     *
     * @param in      The input stream containing the raw bytes of the DNS record.
     * @param message The DNSMessage object containing the record (used for domain name compression).
     * @return A new DNSRecord object representing the decoded record.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
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

    /**
     * Writes the DNS record to an output stream.
     *
     * @param out                 The output stream to write the record bytes to.
     * @param domainNameLocations A map of domain names to their byte offsets (used for compression).
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        DNSMessage.writeDomainName(out, domainNameLocations, name);
        writeTwoBytes(out, type);
        writeTwoBytes(out, clazz);
        writeFourBytes(out, ttl);
        writeTwoBytes(out, rdLength);
        out.write(rdata);
    }

    /**
     * Checks if the DNS record is expired based on its TTL.
     *
     * @return True if the record is expired, false otherwise.
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = creationDate.getTime() + (ttl * 1000);
        return currentTime > expirationTime;
    }

    /**
     * Reads two bytes from an input stream and combines them into a single 16-bit integer.
     *
     * @param in The input stream to read from.
     * @return A 16-bit integer representing the two bytes read.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    private static int readTwoBytes(InputStream in) throws IOException {
        return (in.read() << 8) | in.read();
    }

    /**
     * Reads four bytes from an input stream and combines them into a single 32-bit integer.
     *
     * @param in The input stream to read from.
     * @return A 32-bit integer representing the four bytes read.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    private static long readFourBytes(InputStream in) throws IOException {
        return ((long) in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }

    /**
     * Writes a 16-bit integer to an output stream as two bytes.
     *
     * @param out   The output stream to write to.
     * @param value The 16-bit integer to write.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    private static void writeTwoBytes(ByteArrayOutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Writes a 32-bit integer to an output stream as four bytes.
     *
     * @param out   The output stream to write to.
     * @param value The 32-bit integer to write.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    private static void writeFourBytes(ByteArrayOutputStream out, long value) throws IOException {
        out.write((int) ((value >> 24) & 0xFF));
        out.write((int) ((value >> 16) & 0xFF));
        out.write((int) ((value >> 8) & 0xFF));
        out.write((int) (value & 0xFF));
    }

    /**
     * Returns a string representation of the DNSRecord object.
     *
     * @return A string containing the domain name, record type, class, TTL, and creation date.
     */
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

    /**
     * Returns the domain name associated with the record.
     *
     * @return The domain name as an array of labels.
     */
    public String[] getName() { return name; }

    /**
     * Returns the type of the record.
     *
     * @return The record type (e.g., A, AAAA, MX, etc.).
     */
    public int getType() { return type; }

    /**
     * Returns the class of the record.
     *
     * @return The record class (typically IN for Internet).
     */
    public int getClazz() { return clazz; }

    /**
     * Returns the time-to-live (TTL) for the record.
     *
     * @return The TTL in seconds.
     */
    public long getTtl() { return ttl; }

    /**
     * Returns the length of the resource data.
     *
     * @return The length of the resource data.
     */
    public int getRdLength() { return rdLength; }

    /**
     * Returns the resource data.
     *
     * @return The resource data as a byte array.
     */
    public byte[] getRdata() { return rdata; }

    /**
     * Returns the creation date of the record.
     *
     * @return The timestamp when the record was created.
     */
    public Date getCreationDate() { return creationDate; }
}