import java.util.HashMap;

/**
 * A local cache for DNS records. This cache stores DNS questions and their corresponding answers
 * to reduce the need for repeated queries to external DNS servers.
 */
public class DNSCache {
    private HashMap<DNSQuestion, DNSRecord> cache; // The underlying map storing DNS questions and their records

    /**
     * Constructs a new DNSCache object.
     * Initializes an empty HashMap to store DNS questions and their corresponding records.
     */
    public DNSCache() {
        cache = new HashMap<>();
    }

    /**
     * Queries the cache for a DNS record corresponding to the given DNS question.
     * If the record is found and has not expired, it is returned. Otherwise, the record is removed from the cache.
     *
     * @param question The DNSQuestion object representing the query.
     * @return The DNSRecord object corresponding to the question, or null if no valid record is found.
     */
    public DNSRecord query(DNSQuestion question) {
        DNSRecord record = cache.get(question);
        if (record != null && !record.isExpired()) {
            return record;
        } else {
            cache.remove(question);
            return null;
        }
    }

    /**
     * Inserts a DNS record into the cache for the given DNS question.
     * If a record already exists for the question, it is replaced with the new record.
     *
     * @param question The DNSQuestion object representing the query.
     * @param record   The DNSRecord object representing the answer to the query.
     */
    public void insert(DNSQuestion question, DNSRecord record) {
        cache.put(question, record);
    }
}