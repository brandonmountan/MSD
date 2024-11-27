package lab06;

import java.util.TreeSet;

public class TreeSetPriorityQueue<T extends Comparable<T>> implements PriorityQueue<T> {

    private final TreeSet<T> treeSet;

    /**
     * Constructs an empty priority queue.
     */
    public TreeSetPriorityQueue() {
        this.treeSet = new TreeSet<>();
    }

    @Override
    public void add(T element) {
        treeSet.add(element);
    }

    @Override
    public T removeMin() {
        if (treeSet.isEmpty()) {
            throw new IllegalStateException("Priority queue is empty.");
        }
        return treeSet.pollFirst();
    }

    @Override
    public boolean isEmpty() {
        return treeSet.isEmpty();
    }
}
