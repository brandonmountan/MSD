package assignment06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchTreeTest {

    private BinarySearchTree<Integer> bst;

    @BeforeEach
    public void setUp() {
        bst = new BinarySearchTree<>();
    }

    @Test
    public void testAdd() {
        assertTrue(bst.add(10));
        assertTrue(bst.add(20));
        assertTrue(bst.add(5));
        assertFalse(bst.add(10)); // Duplicate, should not be added
        assertEquals(3, bst.size());
    }

    @Test
    public void testAddAll() {
        List<Integer> numbers = List.of(15, 25, 35);
        assertTrue(bst.addAll(numbers));
        assertEquals(3, bst.size());

        // Adding duplicates
        assertFalse(bst.addAll(List.of(15, 25)));
        assertEquals(3, bst.size());
    }

    @Test
    public void testContains() {
        bst.add(10);
        bst.add(20);
        bst.add(5);

        assertTrue(bst.contains(10));
        assertTrue(bst.contains(5));
        assertFalse(bst.contains(30));
    }

    @Test
    public void testContainsAll() {
        bst.addAll(List.of(10, 20, 30));
        assertTrue(bst.containsAll(List.of(10, 20)));
        assertFalse(bst.containsAll(List.of(10, 40)));
    }

    @Test
    public void testFirst() {
        bst.add(10);
        bst.add(20);
        bst.add(5);

        assertEquals(5, bst.first());

        bst.add(1);
        assertEquals(1, bst.first());
    }

    @Test
    public void testLast() {
        bst.add(10);
        bst.add(20);
        bst.add(5);

        assertEquals(20, bst.last());

        bst.add(25);
        assertEquals(25, bst.last());
    }

    @Test
    public void testRemove() {
        bst.add(10);
        bst.add(20);
        bst.add(5);

        assertTrue(bst.remove(10));
        assertFalse(bst.contains(10));
        assertEquals(2, bst.size());

        assertFalse(bst.remove(30)); // Nonexistent item
    }

    @Test
    public void testRemoveAll() {
        bst.addAll(List.of(10, 20, 30));
        assertTrue(bst.removeAll(List.of(10, 20)));
        assertEquals(1, bst.size());
        assertFalse(bst.contains(10));
        assertFalse(bst.contains(20));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(bst.isEmpty());
        bst.add(10);
        assertFalse(bst.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, bst.size());
        bst.add(10);
        assertEquals(1, bst.size());
        bst.add(20);
        assertEquals(2, bst.size());
    }

    @Test
    public void testClear() {
        bst.add(10);
        bst.add(20);
        bst.add(30);

        bst.clear();
        assertEquals(0, bst.size());
        assertTrue(bst.isEmpty());
    }

    @Test
    public void testToArrayList() {
        bst.add(10);
        bst.add(20);
        bst.add(5);

        ArrayList<Integer> list = bst.toArrayList();
        assertEquals(List.of(5, 10, 20), list);
    }

    @Test
    public void testExceptions() {
        assertThrows(NullPointerException.class, () -> bst.add(null));
        assertThrows(NullPointerException.class, () -> bst.contains(null));
        assertThrows(NullPointerException.class, () -> bst.remove(null));
        assertThrows(NullPointerException.class, () -> bst.addAll(null));

        assertThrows(NoSuchElementException.class, () -> bst.first());
        assertThrows(NoSuchElementException.class, () -> bst.last());
    }
}
