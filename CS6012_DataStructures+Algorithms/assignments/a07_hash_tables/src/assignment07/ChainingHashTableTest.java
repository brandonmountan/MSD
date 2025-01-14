package assignment07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChainingHashTableTest {
    private ChainingHashTable table;
    private HashFunctor goodHashFunctor;

    @BeforeEach
    void setUp() {
        goodHashFunctor = new GoodHashFunctor();
        table = new ChainingHashTable(10, goodHashFunctor);
    }

    @Test
    void testAdd() {
        assertTrue(table.add("apple"));
        assertFalse(table.add("apple")); // Adding duplicate
        assertEquals(1, table.size());
    }

    @Test
    void testAddAll() {
        List<String> items = List.of("apple", "banana", "cherry");
        assertTrue(table.addAll(items));
        assertFalse(table.addAll(items)); // Adding duplicates
        assertEquals(3, table.size());
    }

    @Test
    void testContains() {
        table.add("apple");
        assertTrue(table.contains("apple"));
        assertFalse(table.contains("banana"));
    }

    @Test
    void testContainsAll() {
        table.addAll(List.of("apple", "banana", "cherry"));
        assertTrue(table.containsAll(List.of("apple", "banana")));
        assertFalse(table.containsAll(List.of("apple", "date")));
    }

    @Test
    void testRemove() {
        table.add("apple");
        assertTrue(table.remove("apple"));
        assertFalse(table.remove("apple")); // Removing non-existent item
        assertEquals(0, table.size());
    }

    @Test
    void testRemoveAll() {
        table.addAll(List.of("apple", "banana", "cherry"));
        assertTrue(table.removeAll(List.of("apple", "banana")));
        assertFalse(table.removeAll(List.of("date"))); // Removing non-existent items
        assertEquals(1, table.size());
    }

    @Test
    void testClearAndIsEmpty() {
        table.add("apple");
        assertFalse(table.isEmpty());
        table.clear();
        assertTrue(table.isEmpty());
    }

    @Test
    void testSize() {
        table.add("apple");
        table.add("banana");
        assertEquals(2, table.size());
    }
}
