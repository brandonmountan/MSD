package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadHashFunctorTest {
    @Test
    void testHash() {
        HashFunctor badHash = new BadHashFunctor();
        assertEquals(5, badHash.hash("apple"));  // Length of "apple"
        assertEquals(6, badHash.hash("banana")); // Length of "banana"
        assertEquals(0, badHash.hash(""));       // Empty string
    }
}
