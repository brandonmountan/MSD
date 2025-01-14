package lab06;

public interface PriorityQueue<T extends Comparable<T>> {
    /**
     * Adds an element to the priority queue.
     *
     * @param element the element to add
     */
    void add(T element);

    /**
     * Removes and returns the smallest element in the priority queue.
     *
     * @return the smallest element
     * @throws IllegalStateException if the priority queue is empty
     */
    T removeMin();

    /**
     * Checks if the priority queue is empty.
     *
     * @return true if the priority queue is empty, false otherwise
     */
    boolean isEmpty();
}
