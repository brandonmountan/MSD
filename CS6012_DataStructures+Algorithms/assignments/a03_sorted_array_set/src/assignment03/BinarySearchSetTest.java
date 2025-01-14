package assignment03;

import assignment03.BinarySearchSet;
import assignment03.SortedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchSetTest {
    private BinarySearchSet<Integer> set;
    private BinarySearchSet<String> setWithComparator;

    @BeforeEach
    public void setUp() {
        set = new BinarySearchSet<>();
        setWithComparator = new BinarySearchSet<>((a, b) -> b.compareTo(a));  // Descending order comparator for strings
    }

    // Test the add method and verify ordering
    @Test
    public void testAddAndContains() {
        assertTrue(set.add(10));
        assertTrue(set.add(20));
        assertTrue(set.add(15));
        assertTrue(set.add(5));
        assertFalse(set.add(10));  // Adding duplicate element

        assertTrue(set.contains(10));
        assertTrue(set.contains(15));
        assertFalse(set.contains(100));  // Non-existent element
    }

    // Test if elements are added in sorted order for natural ordering
    @Test
    public void testOrderingWithNaturalOrder() {
        set.add(10);
        set.add(5);
        set.add(20);
        set.add(15);

        Integer[] expectedOrder = {5, 10, 15, 20};
        assertArrayEquals(expectedOrder, set.toArray());
    }

    // Test if elements are added in sorted order with a custom comparator
    @Test
    public void testOrderingWithCustomComparator() {
        setWithComparator.add("apple");
        setWithComparator.add("banana");
        setWithComparator.add("cherry");

        String[] expectedOrder = {"cherry", "banana", "apple"};  // Descending order
        assertArrayEquals(expectedOrder, setWithComparator.toArray());
    }

    // Test the first and last methods
    @Test
    public void testFirstAndLast() {
        set.add(10);
        set.add(5);
        set.add(15);

        assertEquals(5, set.first());
        assertEquals(15, set.last());
    }

    // Test first and last on empty set (should throw exceptions)
    @Test
    public void testFirstAndLastOnEmptySet() {
        assertThrows(NoSuchElementException.class, set::first);
        assertThrows(NoSuchElementException.class, set::last);
    }

    // Test clear and isEmpty methods
    @Test
    public void testClearAndIsEmpty() {
        set.add(10);
        set.add(20);
        assertFalse(set.isEmpty());

        set.clear();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
    }

    // Test addAll method
    @Test
    public void testAddAll() {
        set.addAll(Arrays.asList(1, 2, 3, 4));
        assertTrue(set.contains(1));
        assertTrue(set.contains(4));
        assertEquals(4, set.size());
    }

    // Test remove method
    @Test
    public void testRemove() {
        set.add(10);
        set.add(20);
        set.add(30);

        assertTrue(set.remove(20));
        assertFalse(set.contains(20));
        assertEquals(2, set.size());
    }

    // Test removeAll method
    @Test
    public void testRemoveAll() {
        set.add(10);
        set.add(20);
        set.add(30);
        set.removeAll(Arrays.asList(10, 30));

        assertFalse(set.contains(10));
        assertTrue(set.contains(20));
        assertEquals(1, set.size());
    }

    // Test iterator
    @Test
    public void testIterator() {
        set.add(10);
        set.add(20);
        set.add(15);

        Iterator<Integer> iterator = set.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(10, iterator.next());
        assertEquals(15, iterator.next());
        assertEquals(20, iterator.next());
        assertFalse(iterator.hasNext());
    }

    // Test iterator's NoSuchElementException
    @Test
    public void testIteratorNoSuchElementException() {
        set.add(10);
        Iterator<Integer> iterator = set.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    // Test iterator's remove method
    @Test
    public void testIteratorRemove() {
        set.add(10);
        set.add(20);
        set.add(30);

        Iterator<Integer> iterator = set.iterator();
        iterator.next();  // 10
        iterator.remove();
        assertFalse(set.contains(10));
        assertEquals(2, set.size());
    }

    // Test iterator's remove method without a next call (should throw IllegalStateException)
    @Test
    public void testIteratorRemoveIllegalStateException() {
        set.add(10);
        Iterator<Integer> iterator = set.iterator();
        assertThrows(IllegalStateException.class, iterator::remove);
    }

    // Test toArray
    @Test
    public void testToArray() {
        set.add(10);
        set.add(20);
        set.add(15);

        Integer[] expectedArray = {10, 15, 20};
        assertArrayEquals(expectedArray, set.toArray());
    }

    // Test size
    @Test
    public void testSize() {
        assertEquals(0, set.size());
        set.add(10);
        set.add(20);
        assertEquals(2, set.size());
    }
}
