package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoodHashFunctorTest {
    @Test
    void testHash() {
        HashFunctor goodHash = new GoodHashFunctor();
        assertNotEquals(0, goodHash.hash("apple"));  // Ensure non-zero hash
        assertNotEquals(goodHash.hash("apple"), goodHash.hash("banana")); // Different hashes for different strings
        assertEquals(goodHash.hash("apple"), goodHash.hash("apple"));    // Consistent hash values
    }
}
