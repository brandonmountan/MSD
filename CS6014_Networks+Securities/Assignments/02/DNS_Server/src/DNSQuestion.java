import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DNSQuestion {
    private String[] qName;
    private int qType;
    private int qClass;

    // Private constructor to enforce immutability
    private DNSQuestion(String[] qName, int qType, int qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }

    // Decode a DNS question from an input stream
    public static DNSQuestion decodeQuestion(InputStream in, DNSMessage message) throws IOException {
        String[] qName = message.readDomainName(in);
        int qType = readTwoBytes(in);
        int qClass = readTwoBytes(in);
        return new DNSQuestion(qName, qType, qClass);
    }

    // Write the question to an output stream
    public void writeBytes(ByteArrayOutputStream out, HashMap<String, Integer> domainNameLocations) throws IOException {
        DNSMessage.writeDomainName(out, domainNameLocations, qName);
        writeTwoBytes(out, qType);
        writeTwoBytes(out, qClass);
    }

    // Helper method to read 2 bytes from an input stream
    private static int readTwoBytes(InputStream in) throws IOException {
        return (in.read() << 8) | in.read();
    }

    // Helper method to write 2 bytes to an output stream
    private static void writeTwoBytes(ByteArrayOutputStream out, int value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }

    @Override
    public String toString() {
        return "DNSQuestion{" +
                "qName=" + String.join(".", qName) +
                ", qType=" + qType +
                ", qClass=" + qClass +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNSQuestion that = (DNSQuestion) o;
        return qType == that.qType && qClass == that.qClass && java.util.Arrays.equals(qName, that.qName);
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(qName);
        result = 31 * result + qType;
        result = 31 * result + qClass;
        return result;
    }

    // Getters
    public String[] getQName() { return qName; }
    public int getQType() { return qType; }
    public int getQClass() { return qClass; }
}