package lab06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HeapPriorityQueueTest {

    private HeapPriorityQueue<Integer> heapQueue;

    @BeforeEach
    void setUp() {
        heapQueue = new HeapPriorityQueue<>();
    }

    @Test
    void testAddAndRemoveMin() {
        heapQueue.add(10);
        heapQueue.add(5);
        heapQueue.add(15);

        assertEquals(5, heapQueue.removeMin());
        assertEquals(10, heapQueue.removeMin());
        assertEquals(15, heapQueue.removeMin());
        assertTrue(heapQueue.isEmpty());
    }

    @Test
    void testIsEmpty() {
        assertTrue(heapQueue.isEmpty());
        heapQueue.add(1);
        assertFalse(heapQueue.isEmpty());
        heapQueue.removeMin();
        assertTrue(heapQueue.isEmpty());
    }

    @Test
    void testAddDuplicates() {
        heapQueue.add(5);
        heapQueue.add(5);
        heapQueue.add(5);

        assertEquals(5, heapQueue.removeMin());
        assertEquals(5, heapQueue.removeMin());
        assertEquals(5, heapQueue.removeMin());
        assertTrue(heapQueue.isEmpty());
    }

    @Test
    void testHeapifyConstructor() {
        ArrayList<Integer> unorderedList = new ArrayList<>(Arrays.asList(15, 10, 5, 20, 25));
        HeapPriorityQueue<Integer> heapFromList = new HeapPriorityQueue<>(unorderedList);

        assertEquals(5, heapFromList.removeMin());
        assertEquals(10, heapFromList.removeMin());
        assertEquals(15, heapFromList.removeMin());
        assertEquals(20, heapFromList.removeMin());
        assertEquals(25, heapFromList.removeMin());
        assertTrue(heapFromList.isEmpty());
    }

    @Test
    void testPercolateDown() {
        heapQueue.add(10);
        heapQueue.add(5);
        heapQueue.add(15);
        heapQueue.add(3);

        assertEquals(3, heapQueue.removeMin());
        assertEquals(5, heapQueue.removeMin());
        assertEquals(10, heapQueue.removeMin());
        assertEquals(15, heapQueue.removeMin());
    }

    @Test
    void testRemoveMinThrowsExceptionOnEmptyQueue() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            heapQueue.removeMin();
        });

        assertEquals("Priority queue is empty", exception.getMessage());
    }

    @Test
    void testLargeInput() {
        for (int i = 1000; i >= 1; i--) {
            heapQueue.add(i);
        }

        for (int i = 1; i <= 1000; i++) {
            assertEquals(i, heapQueue.removeMin());
        }

        assertTrue(heapQueue.isEmpty());
    }

    @Test
    void testMixedOperations() {
        heapQueue.add(10);
        heapQueue.add(5);
        heapQueue.add(20);

        assertEquals(5, heapQueue.removeMin());

        heapQueue.add(1);
        heapQueue.add(15);

        assertEquals(1, heapQueue.removeMin());
        assertEquals(10, heapQueue.removeMin());
        assertEquals(15, heapQueue.removeMin());
        assertEquals(20, heapQueue.removeMin());

        assertTrue(heapQueue.isEmpty());
    }
}
