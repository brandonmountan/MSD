package lab06;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class HeapPriorityQueue<T extends Comparable<T>> implements PriorityQueue<T> {

    private final ArrayList<T> heap;

    /**
     * Default constructor for an empty heap.
     */
    public HeapPriorityQueue() {
        heap = new ArrayList<>();
    }

    /**
     * Constructor that builds a heap from an unordered list.
     *
     * @param elements the unordered list of elements
     */
    public HeapPriorityQueue(ArrayList<T> elements) {
        heap = new ArrayList<>(elements);
        heapify();
    }

    @Override
    public void add(T element) {
        heap.add(element);
        percolateUp(heap.size() - 1);
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        T min = heap.get(0);
        // Replace root with the last element and remove the last element
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        if (!isEmpty()) {
            percolateDown(0);
        }
        return min;
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Rearranges the heap to satisfy the heap property.
     */
    private void heapify() {
        for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Ensures the heap property by moving the element at the given index up.
     *
     * @param index the index of the element to percolate up
     */
    private void percolateUp(int index) {
        T element = heap.get(index);
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            T parent = heap.get(parentIndex);
            if (element.compareTo(parent) >= 0) {
                break; // Heap property is satisfied
            }
            heap.set(index, parent); // Move parent down
            index = parentIndex;
        }
        heap.set(index, element); // Place element in correct position
    }

    /**
     * Ensures the heap property by moving the element at the given index down.
     *
     * @param index the index of the element to percolate down
     */
    private void percolateDown(int index) {
        T element = heap.get(index);
        int size = heap.size();
        while (index < size / 2) { // While index has at least one child
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            int smallestChildIndex = leftChildIndex;

            // Find the smaller child
            if (rightChildIndex < size && heap.get(rightChildIndex).compareTo(heap.get(leftChildIndex)) < 0) {
                smallestChildIndex = rightChildIndex;
            }

            // If the element is smaller than the smallest child, stop
            if (element.compareTo(heap.get(smallestChildIndex)) <= 0) {
                break;
            }

            // Move the smaller child up
            heap.set(index, heap.get(smallestChildIndex));
            index = smallestChildIndex;
        }
        heap.set(index, element); // Place element in correct position
    }
}
