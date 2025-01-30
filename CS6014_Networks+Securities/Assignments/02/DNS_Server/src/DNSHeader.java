import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DNSHeader {
    private int id;
    private int flags;
    private int qdCount;
    private int anCount;
    private int nsCount;
    private int arCount;

    // Private constructor to enforce immutability
    private DNSHeader(int id, int flags, int qdCount, int anCount, int nsCount, int arCount) {
        this.id = id;
        this.flags = flags;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
    }

    // Decode a DNS header from an input stream
    public static DNSHeader decodeHeader(InputStream in) throws IOException {
        int id = readTwoBytes(in);
        int flags = readTwoBytes(in);
        int qdCount = readTwoBytes(in);
        int anCount = readTwoBytes(in);
        int nsCount = readTwoBytes(in);
        int arCount = readTwoBytes(in);
        return new DNSHeader(id, flags, qdCount, anCount, nsCount, arCount);
    }

    // Build a DNS header for a response
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

    // Build a DNS header for a request
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

    // Write the header to an output stream
    public void writeBytes(OutputStream out) throws IOException {
        writeTwoBytes(out, id);
        writeTwoBytes(out, flags);
        writeTwoBytes(out, qdCount);
        writeTwoBytes(out, anCount);
        writeTwoBytes(out, nsCount);
        writeTwoBytes(out, arCount);
    }

    // Helper method to read 2 bytes from an input stream
    private static int readTwoBytes(InputStream in) throws IOException {
        return (in.read() << 8) | in.read();
    }

    // Helper method to write 2 bytes to an output stream
    private static void writeTwoBytes(OutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

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
    public int getId() { return id; }
    public int getFlags() { return flags; }
    public int getQdCount() { return qdCount; }
    public int getAnCount() { return anCount; }
    public int getNsCount() { return nsCount; }
    public int getArCount() { return arCount; }
}