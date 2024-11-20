package assignment05;

import java.util.NoSuchElementException;

/**
 * A stack implementation backed by a singly linked list.
 *
 * @param <E> the type of elements in this stack
 */
public class LinkedListStack<E> implements Stack<E> {
    private SinglyLinkedList<E> list;

    /**
     * Constructs an empty stack.
     */
    public LinkedListStack() {
        this.list = new SinglyLinkedList<>();
    }

    /**
     * Pushes the specified element onto the stack.
     *
     * @param element the element to push onto the stack
     */
    @Override
    public void push(E element) {
        list.insertFirst(element);
    }

    /**
     * Pops and returns the top element from the stack.
     *
     * @return the top element of the stack
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E pop() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return list.deleteFirst();
    }

    /**
     * Returns the top element of the stack without removing it.
     *
     * @return the top element of the stack
     * @throws NoSuchElementException if the stack is empty
     */
    @Override
    public E peek() {
        if (list.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return list.getFirst();
    }

    /**
     * Returns whether the stack is empty.
     *
     * @return true if the stack is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes all elements from the stack.
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Returns the number of elements in the stack.
     *
     * @return the number of elements in the stack
     */
    @Override
    public int size() {
        return list.size();
    }
}
