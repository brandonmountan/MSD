import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A DNS server that listens for incoming DNS requests, checks its local cache for valid responses,
 * and forwards requests to Google's public DNS server if no valid response is found in the cache.
 */
public class DNSServer {
    private static final int PORT = 8053;               // The port on which the DNS server listens
    private static final String GOOGLE_DNS_SERVER = "8.8.8.8"; // Google's public DNS server address
    private static final int GOOGLE_DNS_PORT = 53;      // The standard DNS port for Google's server
    private static DNSCache cache = new DNSCache();     // The local cache for DNS records

    /**
     * The main entry point for the DNS server.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("DNS Server is running on port " + PORT);
            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                // Decode the incoming DNS request
                DNSMessage requestMessage = DNSMessage.decodeMessage(requestPacket.getData());

                // Handle the request and build a response
                DNSMessage responseMessage = handleRequest(requestMessage);

                // Send the response back to the client
                byte[] responseData = responseMessage.toBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an incoming DNS request by checking the cache and forwarding the request to Google's DNS server if necessary.
     *
     * @param requestMessage The DNSMessage object representing the incoming request.
     * @return A DNSMessage object representing the response to the request.
     * @throws IOException If an I/O error occurs while querying Google's DNS server.
     */
    private static DNSMessage handleRequest(DNSMessage requestMessage) throws IOException {
        DNSRecord[] answers = new DNSRecord[requestMessage.getQuestions().length];

        // Process each question in the request
        for (int i = 0; i < requestMessage.getQuestions().length; i++) {
            DNSQuestion question = requestMessage.getQuestions()[i];

            // Check the cache for a valid answer
            DNSRecord cachedAnswer = cache.query(question);
            if (cachedAnswer != null) {
                answers[i] = cachedAnswer;
            } else {
                // Query Google's DNS server if no valid answer is found in the cache
                DNSRecord googleAnswer = queryGoogle(question);
                if (googleAnswer != null) {
                    // Cache the answer and include it in the response
                    cache.insert(question, googleAnswer);
                    answers[i] = googleAnswer;
                }
            }
        }

        // Build and return the response message
        return DNSMessage.buildResponse(requestMessage, answers);
    }

    /**
     * Queries Google's DNS server for a DNS question.
     *
     * @param question The DNSQuestion object representing the query.
     * @return A DNSRecord object representing the first answer from Google's DNS server, or null if no answer is found.
     * @throws IOException If an I/O error occurs while communicating with Google's DNS server.
     */
    private static DNSRecord queryGoogle(DNSQuestion question) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Build the DNS request message
            byte[] requestData = buildRequest(question);

            // Send the request to Google's DNS server
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, InetAddress.getByName(GOOGLE_DNS_SERVER), GOOGLE_DNS_PORT);
            socket.send(requestPacket);

            // Receive the response from Google's DNS server
            byte[] responseBuffer = new byte[512];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);

            // Decode the response and return the first answer
            DNSMessage googleResponse = DNSMessage.decodeMessage(responsePacket.getData());
            if (googleResponse.getAnswers().length > 0) {
                return googleResponse.getAnswers()[0];
            }
        }
        return null;
    }

    /**
     * Builds a DNS request message for a given DNS question.
     *
     * @param question The DNSQuestion object representing the query.
     * @return A byte array representing the DNS request message.
     * @throws IOException If an I/O error occurs while building the request.
     */
    private static byte[] buildRequest(DNSQuestion question) throws IOException {
        DNSMessage requestMessage = DNSMessage.buildRequestMessage(question);
        return requestMessage.toBytes();
    }
}