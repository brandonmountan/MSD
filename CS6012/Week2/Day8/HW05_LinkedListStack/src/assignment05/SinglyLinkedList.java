package assignment05;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic implementation of a singly linked list.
 *
 * @param <E> the type of elements in this list
 */
public class SinglyLinkedList<E> implements List<E> {
    private Node<E> head;
    private int size;

    /**
     * Represents a single node in the singly linked list.
     *
     * @param <E> the type of element stored in the node
     */
    private static class Node<E> {
        // holds element stored in the node
        E data;
        // points to next node in list (null if last node)
        Node<E> next;

        /**
         * Constructs a node with the given data.
         *
         * @param data the data to store in the node
         */
        Node(E data) {
            this.data = data;
        }
    }

    /**
     * Constructs an empty singly linked list. (no elements)
     */
    public SinglyLinkedList() {
        // points to first node in list (null if empty)
        this.head = null;
        // track number of elements in list
        this.size = 0;
    }

    /**
     * Inserts the specified element at the beginning of the list.
     *
     * @param element the element to insert
     */
    @Override
    // O(1) - no traversal
    public void insertFirst(E element) {
        // create new node with given element
        Node<E> newNode = new Node<>(element);
        // set next of new node to current head
        newNode.next = head;
        // update head to new node
        head = newNode;
        size++;
    }

    /**
     * Inserts the specified element at the given index in the list.
     *
     * @param index   the index at which the element is to be inserted
     * @param element the element to insert
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    // O(N) - traverses up to N - 1 nodes
    public void insert(int index, E element) {
        // edge cases
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (index == 0) {
            insertFirst(element);
            return;
        }
        // traverse to node just before specified index
        Node<E> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        // create new node
        Node<E> newNode = new Node<>(element);
        // link it between preceding node and next node
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    /**
     * Returns the first element in the list.
     *
     * @return the first element in the list
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    /**
     * Returns the element at the specified index in the list.
     *
     * @param index the index of the element to return
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    // O(N) - traversing N-1 nodes
    public E get(int index) {
        // validate index
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        // traverse list to specified index
        Node<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        // return data of node at that index
        return current.data;
    }

    /**
     * Deletes and returns the first element in the list.
     *
     * @return the deleted element
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E deleteFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        // store data of head node
        E data = head.data;
        // update head to next node
        head = head.next;
        size--;
        return data;
    }

    /**
     * Deletes and returns the element at the specified index.
     *
     * @param index the index of the element to delete
     * @return the deleted element
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    @Override
    // O(N)
    public E delete(int index) {
        // validate index
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        if (index == 0) {
            return deleteFirst();
        }
        // traverse to node before target node
        Node<E> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        // update next reference to bypass target node
        E data = current.next.data;
        current.next = current.next.next;
        size--;
        return data;
    }

    /**
     * Returns the index of the specified element in the list, or -1 if the element is not found.
     *
     * @param element the element to search for
     * @return the index of the element, or -1 if not found
     */
    @Override
    // O(N) - may need to traverse the entire list
    public int indexOf(E element) {
        Node<E> current = head;
        int index = 0;
        // traverse the list while comparing each node's data with the target element
        while (current != null) {
            if ((element == null && current.data == null) || (element != null && element.equals(current.data))) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return the number of elements in the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns whether the list is empty.
     *
     * @return true if the list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements from the list.
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns an array containing all elements in the list in proper sequence.
     *
     * @return an array containing all elements in the list
     */
    @Override
    // O(N)
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node<E> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }
        return array;
    }

    /**
     * Returns an iterator over elements in the list. (for traversal)
     *
     * @return an iterator over elements in the list
     */
    @Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator();
    }

    /**
     * An iterator for the SinglyLinkedList class.
     */
    private class SinglyLinkedListIterator implements Iterator<E> {
        private Node<E> current = head;
        private Node<E> previous = null;
        private boolean canRemove = false;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            canRemove = true;
            E data = current.data;
            previous = current;
            current = current.next;
            return data;
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("Cannot remove element before calling next()");
            }

            if (previous == head) { // If removing the first element
                deleteFirst();
            } else {
                Node<E> temp = head;
                while (temp.next != previous) {
                    temp = temp.next;
                }
                temp.next = current;
                size--;
            }
            canRemove = false;
        }
    }
}
