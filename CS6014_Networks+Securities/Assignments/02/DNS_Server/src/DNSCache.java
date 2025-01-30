import java.util.HashMap;

public class DNSCache {
    private HashMap<DNSQuestion, DNSRecord> cache;

    public DNSCache() {
        cache = new HashMap<>();
    }

    // Query the cache for a DNS record
    public DNSRecord query(DNSQuestion question) {
        DNSRecord record = cache.get(question);
        if (record != null && !record.isExpired()) {
            return record;
        } else {
            cache.remove(question);
            return null;
        }
    }

    // Insert a DNS record into the cache
    public void insert(DNSQuestion question, DNSRecord record) {
        cache.put(question, record);
    }
}