import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the header section of a DNS message.
 * The header contains metadata about the DNS query or response, such as the ID, flags, and counts of questions, answers, authority records, and additional records.
 */
public class DNSHeader {
    private int id;          // 16-bit identifier for the DNS query/response
    private int flags;       // 16-bit flags field (e.g., query/response, error codes)
    private int qdCount;     // Number of questions in the DNS message
    private int anCount;     // Number of answers in the DNS message
    private int nsCount;     // Number of authority records in the DNS message
    private int arCount;     // Number of additional records in the DNS message

    /**
     * Private constructor to enforce immutability.
     *
     * @param id      The 16-bit identifier for the DNS query/response.
     * @param flags   The 16-bit flags field.
     * @param qdCount The number of questions in the DNS message.
     * @param anCount The number of answers in the DNS message.
     * @param nsCount The number of authority records in the DNS message.
     * @param arCount The number of additional records in the DNS message.
     */
    private DNSHeader(int id, int flags, int qdCount, int anCount, int nsCount, int arCount) {
        this.id = id;
        this.flags = flags;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
    }

    /**
     * Decodes a DNS header from an input stream.
     *
     * @param in The input stream containing the raw bytes of the DNS header.
     * @return A new DNSHeader object representing the decoded header.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    public static DNSHeader decodeHeader(InputStream in) throws IOException {
        int id = readTwoBytes(in);
        int flags = readTwoBytes(in);
        int qdCount = readTwoBytes(in);
        int anCount = readTwoBytes(in);
        int nsCount = readTwoBytes(in);
        int arCount = readTwoBytes(in);
        return new DNSHeader(id, flags, qdCount, anCount, nsCount, arCount);
    }

    /**
     * Builds a DNS header for a response message.
     *
     * @param request  The DNSMessage object representing the request.
     * @param response The DNSMessage object representing the response.
     * @return A new DNSHeader object for the response.
     */
    public static DNSHeader buildHeaderForResponse(DNSMessage request, DNSMessage response) {
        return new DNSHeader(
                request.getHeader().getId(),
                0x8180, // Standard query response, no error
                request.getHeader().getQdCount(),
                response.getAnswers().length,
                0,
                0
        );
    }

    /**
     * Builds a DNS header for a request message.
     *
     * @return A new DNSHeader object for the request.
     */
    public static DNSHeader buildHeaderForRequest() {
        return new DNSHeader(
                1234, // Example ID
                0x0100, // Standard query
                1, // One question
                0, // No answers
                0, // No authority records
                0 // No additional records
        );
    }

    /**
     * Writes the DNS header fields to an output stream.
     *
     * @param out The output stream to write the header bytes to.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public void writeBytes(OutputStream out) throws IOException {
        writeTwoBytes(out, id);
        writeTwoBytes(out, flags);
        writeTwoBytes(out, qdCount);
        writeTwoBytes(out, anCount);
        writeTwoBytes(out, nsCount);
        writeTwoBytes(out, arCount);
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
    private static void writeTwoBytes(OutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    /**
     * Returns a string representation of the DNSHeader object.
     *
     * @return A string containing the values of the header fields.
     */
    @Override
    public String toString() {
        return "DNSHeader{" +
                "id=" + id +
                ", flags=" + flags +
                ", qdCount=" + qdCount +
                ", anCount=" + anCount +
                ", nsCount=" + nsCount +
                ", arCount=" + arCount +
                '}';
    }

    // Getters

    /**
     * Returns the ID of the DNS header.
     *
     * @return The 16-bit identifier for the DNS query/response.
     */
    public int getId() { return id; }

    /**
     * Returns the flags of the DNS header.
     *
     * @return The 16-bit flags field.
     */
    public int getFlags() { return flags; }

    /**
     * Returns the number of questions in the DNS message.
     *
     * @return The number of questions.
     */
    public int getQdCount() { return qdCount; }

    /**
     * Returns the number of answers in the DNS message.
     *
     * @return The number of answers.
     */
    public int getAnCount() { return anCount; }

    /**
     * Returns the number of authority records in the DNS message.
     *
     * @return The number of authority records.
     */
    public int getNsCount() { return nsCount; }

    /**
     * Returns the number of additional records in the DNS message.
     *
     * @return The number of additional records.
     */
    public int getArCount() { return arCount; }
}