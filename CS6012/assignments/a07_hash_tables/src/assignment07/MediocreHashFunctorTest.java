package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediocreHashFunctorTest {
    @Test
    void testHash() {
        HashFunctor mediocreHash = new MediocreHashFunctor();
        assertEquals(532, mediocreHash.hash("apple"));   // Sum of ASCII values of 'a', 'p', 'p', 'l', 'e'
        assertEquals(609, mediocreHash.hash("banana"));  // Sum of ASCII values
        assertEquals(0, mediocreHash.hash(""));          // Empty string
    }
}
