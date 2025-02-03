import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Represents a complete DNS message, including the header, questions, answers, authority records, and additional records.
 * This class provides methods for decoding, encoding, and building DNS messages.
 */
public class DNSMessage {
    private DNSHeader header;               // The header section of the DNS message
    private DNSQuestion[] questions;        // The question section of the DNS message
    private DNSRecord[] answers;            // The answer section of the DNS message
    private DNSRecord[] authorityRecords;   // The authority records section of the DNS message
    private DNSRecord[] additionalRecords;  // The additional records section of the DNS message
    private byte[] completeMessage;         // The raw byte array of the complete DNS message

    /**
     * Private constructor to enforce immutability.
     *
     * @param header            The header section of the DNS message.
     * @param questions         The question section of the DNS message.
     * @param answers           The answer section of the DNS message.
     * @param authorityRecords  The authority records section of the DNS message.
     * @param additionalRecords The additional records section of the DNS message.
     * @param completeMessage   The raw byte array of the complete DNS message.
     */
    private DNSMessage(DNSHeader header, DNSQuestion[] questions, DNSRecord[] answers,
                       DNSRecord[] authorityRecords, DNSRecord[] additionalRecords, byte[] completeMessage) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.authorityRecords = authorityRecords;
        this.additionalRecords = additionalRecords;
        this.completeMessage = completeMessage;
    }

    /**
     * Decodes a DNS message from a byte array.
     *
     * @param bytes The byte array containing the raw DNS message.
     * @return A new DNSMessage object representing the decoded message.
     * @throws IOException If an I/O error occurs while reading from the byte array.
     */
    public static DNSMessage decodeMessage(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        DNSHeader header = DNSHeader.decodeHeader(in);
        DNSQuestion[] questions = new DNSQuestion[header.getQdCount()];
        for (int i = 0; i < questions.length; i++) {
            questions[i] = DNSQuestion.decodeQuestion(in, new DNSMessage(null, null, null, null, null, bytes));
        }
        DNSRecord[] answers = new DNSRecord[header.getAnCount()];
        for (int i = 0; i < answers.length; i++) {
            answers[i] = DNSRecord.decodeRecord(in, new DNSMessage(null, null, null, null, null, bytes));
        }
        DNSRecord[] authorityRecords = new DNSRecord[header.getNsCount()];
        for (int i = 0; i < authorityRecords.length; i++) {
            authorityRecords[i] = DNSRecord.decodeRecord(in, new DNSMessage(null, null, null, null, null, bytes));
        }
        DNSRecord[] additionalRecords = new DNSRecord[header.getArCount()];
        for (int i = 0; i < additionalRecords.length; i++) {
            additionalRecords[i] = DNSRecord.decodeRecord(in, new DNSMessage(null, null, null, null, null, bytes));
        }
        return new DNSMessage(header, questions, answers, authorityRecords, additionalRecords, bytes);
    }

    /**
     * Builds a DNS request message.
     *
     * @param question The DNS question to include in the request.
     * @return A new DNSMessage object representing the request.
     */
    public static DNSMessage buildRequestMessage(DNSQuestion question) {
        DNSHeader header = DNSHeader.buildHeaderForRequest();
        DNSQuestion[] questions = new DNSQuestion[]{question};
        return new DNSMessage(header, questions, new DNSRecord[0], new DNSRecord[0], new DNSRecord[0], null);
    }

    /**
     * Builds a DNS response message.
     *
     * @param request The DNSMessage object representing the request.
     * @param answers The DNS records to include as answers in the response.
     * @return A new DNSMessage object representing the response.
     */
    public static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers) {
        DNSHeader header = DNSHeader.buildHeaderForResponse(request, new DNSMessage(null, null, answers, null, null, null));
        return new DNSMessage(header, request.getQuestions(), answers, new DNSRecord[0], new DNSRecord[0], null);
    }

    /**
     * Reads a domain name from an input stream.
     *
     * @param in The input stream to read from.
     * @return An array of strings representing the domain name labels.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    public String[] readDomainName(InputStream in) throws IOException {
        return readDomainName(in, new StringBuilder());
    }

    /**
     * Reads a domain name from a specific position in the message.
     *
     * @param firstByte The byte index in the message where the domain name starts.
     * @return An array of strings representing the domain name labels.
     * @throws IOException If an I/O error occurs while reading from the message.
     */
    public String[] readDomainName(int firstByte) throws IOException {
        ByteArrayInputStream newIn = new ByteArrayInputStream(completeMessage);
        newIn.skip(firstByte);
        return readDomainName(newIn, new StringBuilder());
    }

    /**
     * Helper method to read a domain name from an input stream.
     *
     * @param in The input stream to read from.
     * @param sb A StringBuilder to accumulate the domain name labels.
     * @return An array of strings representing the domain name labels.
     * @throws IOException If an I/O error occurs while reading from the input stream.
     */
    private String[] readDomainName(InputStream in, StringBuilder sb) throws IOException {
        int length = in.read();
        if (length == 0) {
            return sb.toString().split("\\.");
        }
        if ((length & 0xC0) == 0xC0) {
            int pointer = ((length & 0x3F) << 8) | in.read();
            return readDomainName(pointer);
        }
        byte[] buffer = new byte[length];
        in.read(buffer);
        sb.append(new String(buffer));
        sb.append(".");
        return readDomainName(in, sb);
    }

    /**
     * Writes a domain name to an output stream.
     *
     * @param out              The output stream to write to.
     * @param domainLocations  A map of domain names to their byte offsets (used for compression).
     * @param domainPieces     An array of strings representing the domain name labels.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public static void writeDomainName(ByteArrayOutputStream out, HashMap<String, Integer> domainLocations, String[] domainPieces) throws IOException {
        String domainName = String.join(".", domainPieces);
        if (domainLocations.containsKey(domainName)) {
            int pointer = domainLocations.get(domainName);
            out.write((pointer >> 8) | 0xC0);
            out.write(pointer & 0xFF);
        } else {
            domainLocations.put(domainName, out.size());
            for (String piece : domainPieces) {
                out.write(piece.length());
                out.write(piece.getBytes());
            }
            out.write(0);
        }
    }

    /**
     * Converts the DNS message to a byte array.
     *
     * @return A byte array representing the DNS message.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        header.writeBytes(out);
        HashMap<String, Integer> domainNameLocations = new HashMap<>();
        for (DNSQuestion question : questions) {
            question.writeBytes(out, domainNameLocations);
        }
        for (DNSRecord answer : answers) {
            answer.writeBytes(out, domainNameLocations);
        }
        return out.toByteArray();
    }

    /**
     * Returns a string representation of the DNSMessage object.
     *
     * @return A string containing the header, questions, answers, authority records, and additional records.
     */
    @Override
    public String toString() {
        return "DNSMessage{" +
                "header=" + header +
                ", questions=" + java.util.Arrays.toString(questions) +
                ", answers=" + java.util.Arrays.toString(answers) +
                ", authorityRecords=" + java.util.Arrays.toString(authorityRecords) +
                ", additionalRecords=" + java.util.Arrays.toString(additionalRecords) +
                '}';
    }

    // Getters

    /**
     * Returns the header section of the DNS message.
     *
     * @return The DNSHeader object representing the header.
     */
    public DNSHeader getHeader() { return header; }

    /**
     * Returns the question section of the DNS message.
     *
     * @return An array of DNSQuestion objects representing the questions.
     */
    public DNSQuestion[] getQuestions() { return questions; }

    /**
     * Returns the answer section of the DNS message.
     *
     * @return An array of DNSRecord objects representing the answers.
     */
    public DNSRecord[] getAnswers() { return answers; }

    /**
     * Returns the authority records section of the DNS message.
     *
     * @return An array of DNSRecord objects representing the authority records.
     */
    public DNSRecord[] getAuthorityRecords() { return authorityRecords; }

    /**
     * Returns the additional records section of the DNS message.
     *
     * @return An array of DNSRecord objects representing the additional records.
     */
    public DNSRecord[] getAdditionalRecords() { return additionalRecords; }

    /**
     * Returns the raw byte array of the complete DNS message.
     *
     * @return The byte array representing the complete DNS message.
     */
    public byte[] getCompleteMessage() { return completeMessage; }
}