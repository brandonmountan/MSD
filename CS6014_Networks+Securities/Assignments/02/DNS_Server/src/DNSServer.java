import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSServer {
    private static final int PORT = 8053;
    private static final String GOOGLE_DNS_SERVER = "8.8.8.8";
    private static final int GOOGLE_DNS_PORT = 53;
    private static DNSCache cache = new DNSCache();

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("DNS Server is running on port " + PORT);
            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                DNSMessage requestMessage = DNSMessage.decodeMessage(requestPacket.getData());
                DNSMessage responseMessage = handleRequest(requestMessage);

                byte[] responseData = responseMessage.toBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DNSMessage handleRequest(DNSMessage requestMessage) throws IOException {
        DNSRecord[] answers = new DNSRecord[requestMessage.getQuestions().length];

        for (int i = 0; i < requestMessage.getQuestions().length; i++) {
            DNSQuestion question = requestMessage.getQuestions()[i];
            DNSRecord cachedAnswer = cache.query(question);
            if (cachedAnswer != null) {
                answers[i] = cachedAnswer;
            } else {
                DNSRecord googleAnswer = queryGoogle(question);
                if (googleAnswer != null) {
                    cache.insert(question, googleAnswer);
                    answers[i] = googleAnswer;
                }
            }
        }

        return DNSMessage.buildResponse(requestMessage, answers);
    }

    private static DNSRecord queryGoogle(DNSQuestion question) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] requestData = buildRequest(question);
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, InetAddress.getByName(GOOGLE_DNS_SERVER), GOOGLE_DNS_PORT);
            socket.send(requestPacket);

            byte[] responseBuffer = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            DNSMessage googleResponse = DNSMessage.decodeMessage(responsePacket.getData());
            if (googleResponse.getAnswers().length > 0) {
                return googleResponse.getAnswers()[0];
            }
        }
        return null;
    }

    private static byte[] buildRequest(DNSQuestion question) throws IOException {
        DNSMessage requestMessage = DNSMessage.buildRequestMessage(question);
        return requestMessage.toBytes();
    }
}