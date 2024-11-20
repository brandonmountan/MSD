package assignment05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

class LinkedListStackTest {

    private LinkedListStack<String> stack;

    @BeforeEach
    void setUp() {
        stack = new LinkedListStack<>();
    }

    @Test
    void testPush() {
        stack.push("A");
        assertEquals(1, stack.size());
        assertEquals("A", stack.peek());
    }

    @Test
    void testPop() {
        stack.push("A");
        stack.push("B");
        String popped = stack.pop();
        assertEquals("B", popped);
        assertEquals(1, stack.size());
    }

    @Test
    void testPeek() {
        stack.push("A");
        assertEquals("A", stack.peek());
    }

    @Test
    void testPopEmptyStack() {
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }

    @Test
    void testPeekEmptyStack() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }

    @Test
    void testIsEmpty() {
        assertTrue(stack.isEmpty());
        stack.push("A");
        assertFalse(stack.isEmpty());
    }

    @Test
    void testClear() {
        stack.push("A");
        stack.clear();
        assertTrue(stack.isEmpty());
    }

    @Test
    void testSize() {
        assertEquals(0, stack.size());
        stack.push("A");
        stack.push("B");
        assertEquals(2, stack.size());
    }
}
