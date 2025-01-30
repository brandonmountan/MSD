import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class DNSMessage {
    private DNSHeader header;
    private DNSQuestion[] questions;
    private DNSRecord[] answers;
    private DNSRecord[] authorityRecords;
    private DNSRecord[] additionalRecords;
    private byte[] completeMessage;

    // Private constructor to enforce immutability
    private DNSMessage(DNSHeader header, DNSQuestion[] questions, DNSRecord[] answers,
                       DNSRecord[] authorityRecords, DNSRecord[] additionalRecords, byte[] completeMessage) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.authorityRecords = authorityRecords;
        this.additionalRecords = additionalRecords;
        this.completeMessage = completeMessage;
    }

    // Decode a DNS message from a byte array
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

    // Build a DNS request message
    public static DNSMessage buildRequestMessage(DNSQuestion question) {
        DNSHeader header = DNSHeader.buildHeaderForRequest();
        DNSQuestion[] questions = new DNSQuestion[]{question};
        return new DNSMessage(header, questions, new DNSRecord[0], new DNSRecord[0], new DNSRecord[0], null);
    }

    // Build a DNS response message
    public static DNSMessage buildResponse(DNSMessage request, DNSRecord[] answers) {
        DNSHeader header = DNSHeader.buildHeaderForResponse(request, new DNSMessage(null, null, answers, null, null, null));
        return new DNSMessage(header, request.getQuestions(), answers, new DNSRecord[0], new DNSRecord[0], null);
    }

    // Read a domain name from an input stream
    public String[] readDomainName(InputStream in) throws IOException {
        return readDomainName(in, new StringBuilder());
    }

    // Read a domain name from a specific position in the message
    public String[] readDomainName(int firstByte) throws IOException {
        ByteArrayInputStream newIn = new ByteArrayInputStream(completeMessage);
        newIn.skip(firstByte);
        return readDomainName(newIn, new StringBuilder());
    }

    // Helper method to read a domain name
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

    // Write a domain name to an output stream
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

    // Convert the message to a byte array
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
    public DNSHeader getHeader() { return header; }
    public DNSQuestion[] getQuestions() { return questions; }
    public DNSRecord[] getAnswers() { return answers; }
    public DNSRecord[] getAuthorityRecords() { return authorityRecords; }
    public DNSRecord[] getAdditionalRecords() { return additionalRecords; }
    public byte[] getCompleteMessage() { return completeMessage; }
}