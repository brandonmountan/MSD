package assignment05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;


import java.util.NoSuchElementException;

class SinglyLinkedListTest {

    private SinglyLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new SinglyLinkedList<>();
    }

    @Test
    void testInsertFirst() {
        list.insertFirst("A");
        assertEquals(1, list.size());
        assertEquals("A", list.getFirst());
    }

    @Test
    void testInsertAtIndex() {
        list.insertFirst("A");
        list.insert(1, "B");
        assertEquals(2, list.size());
        assertEquals("B", list.get(1));
    }

    @Test
    void testDeleteFirst() {
        list.insertFirst("A");
        list.insertFirst("B");
        String removed = list.deleteFirst();
        assertEquals("B", removed);
        assertEquals(1, list.size());
    }

    @Test
    void testDeleteAtIndex() {
        list.insertFirst("A");
        list.insertFirst("B");
        String removed = list.delete(0);
        assertEquals("B", removed);
        assertEquals(1, list.size());
    }

    @Test
    void testIndexOf() {
        list.insertFirst("A");
        list.insertFirst("B");
        assertEquals(1, list.indexOf("A"));
        assertEquals(-1, list.indexOf("C"));
    }

    @Test
    void testGet() {
        list.insertFirst("A");
        list.insertFirst("B");
        assertEquals("A", list.get(1));
        assertEquals("B", list.get(0));
    }

    @Test
    void testIsEmpty() {
        assertTrue(list.isEmpty());
        list.insertFirst("A");
        assertFalse(list.isEmpty());
    }

    @Test
    void testClear() {
        list.insertFirst("A");
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void testToArray() {
        list.insertFirst("A");
        list.insertFirst("B");
        Object[] array = list.toArray();
        assertEquals("B", array[0]);
        assertEquals("A", array[1]);
    }

    @Test
    void testIterator() {
        list.insertFirst("A");
        list.insertFirst("B");
        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertFalse(iterator.hasNext());
    }
}
