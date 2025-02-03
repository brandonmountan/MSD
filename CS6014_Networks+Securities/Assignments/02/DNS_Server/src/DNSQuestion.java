import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Represents the question section of a DNS message.
 * The question section contains the domain name being queried, the type of query, and the class of query.
 */
public class DNSQuestion {
    private String[] qName; // The domain name being queried, split into labels
    private int qType;      // The type of query (e.g., A, AAAA, MX, etc.)
    private int qClass;     // The class of query (typically IN for Internet)

    /**
     * Private constructor to enforce immutability.
     *
     * @param qName  The domain name being queried, split into labels.
     * @param qType  The type of query.
     * @param qClass The class of query.
     */
    private DNSQuestion(String[] qName, int qType, int qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }

    /**
     * Decodes a DNS question from an input stream.
     *
     * @param in      The input stream containing the raw bytes of the DNS question.
     * @param message The DNSMessage object containing the question (used for domain name compression).
     * @return A new DNSQuestion object representing the decoded question.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    public static DNSQuestion decodeQuestion(InputStream in, DNSMessage message) throws IOException {
        String[] qName = message.readDomainName(in);
        int qType = readTwoBytes(in);
        int qClass = readTwoBytes(in);
        return new DNSQuestion(qName, qType, qClass);
    }

    /**
     * Writes the DNS question to an output stream.
     *
     * @param out                 The output stream to write the question bytes to.
     * @param domainNameLocations A map of domain names to their byte offsets (used for compression).
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        DNSMessage.writeDomainName(out, domainNameLocations, qName);
        writeTwoBytes(out, qType);
        writeTwoBytes(out, qClass);
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
     * Returns a string representation of the DNSQuestion object.
     *
     * @return A string containing the domain name, query type, and query class.
     */
    @Override
    public String toString() {
        return "DNSQuestion{" +
                "qName=" + String.join(".", qName) +
                ", qType=" + qType +
                ", qClass=" + qClass +
                '}';
    }

    /**
     * Compares this DNSQuestion object to another object for equality.
     *
     * @param o The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNSQuestion that = (DNSQuestion) o;
        return qType == that.qType && qClass == that.qClass && java.util.Arrays.equals(qName, that.qName);
    }

    /**
     * Returns a hash code for the DNSQuestion object.
     *
     * @return A hash code based on the domain name, query type, and query class.
     */
    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(qName);
        result = 31 * result + qType;
        result = 31 * result + qClass;
        return result;
    }

    // Getters

    /**
     * Returns the domain name being queried.
     *
     * @return The domain name as an array of labels.
     */
    public String[] getQName() { return qName; }

    /**
     * Returns the type of query.
     *
     * @return The query type (e.g., A, AAAA, MX, etc.).
     */
    public int getQType() { return qType; }

    /**
     * Returns the class of query.
     *
     * @return The query class (typically IN for Internet).
     */
    public int getQClass() { return qClass; }
}