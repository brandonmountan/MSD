package assignment07;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A hash table that uses separate chaining to resolve collisions.
 */
public class ChainingHashTable implements Set<String> {
    private LinkedList<String>[] storage;
    private HashFunctor functor;
    private int size;

    /**
     * Constructs a ChainingHashTable with a specified capacity and hash functor.
     *
     * @param capacity The initial capacity of the table.
     * @param functor  The hash function to use for determining bucket placement.
     */
    @SuppressWarnings("unchecked")
    public ChainingHashTable(int capacity, HashFunctor functor) {
        this.storage = (LinkedList<String>[]) new LinkedList[capacity];
        this.functor = functor;
        this.size = 0;

        // Initialize each bucket as an empty LinkedList
        for (int i = 0; i < capacity; i++) {
            storage[i] = new LinkedList<>();
        }
    }

    /**
     * Adds the specified item to the hash table if it is not already present.
     *
     * @param item The item to add.
     * @return {@code true} if the item was added successfully, {@code false} if it was already present.
     */
    @Override
    public boolean add(String item) {
        int index = Math.abs(functor.hash(item) % storage.length);
        if (!storage[index].contains(item)) {
            storage[index].add(item);
            size++;
            return true;
        }
        return false;
    }

    /**
     * Adds all the items in the specified collection to the hash table.
     *
     * @param items The collection of items to add.
     * @return {@code true} if at least one item was added, {@code false} otherwise.
     */
    @Override
    public boolean addAll(Collection<? extends String> items) {
        boolean modified = false;
        for (String item : items) {
            if (add(item)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all items from the hash table, leaving it empty.
     */
    @Override
    public void clear() {
        for (LinkedList<String> bucket : storage) {
            bucket.clear();
        }
        size = 0;
    }

    /**
     * Checks whether the specified item is present in the hash table.
     *
     * @param item The item to check for.
     * @return {@code true} if the item is found, {@code false} otherwise.
     */
    @Override
    public boolean contains(String item) {
        int index = Math.abs(functor.hash(item) % storage.length);
        return storage[index].contains(item);
    }

    /**
     * Checks whether all the items in the specified collection are present in the hash table.
     *
     * @param items The collection of items to check for.
     * @return {@code true} if all items are found, {@code false} otherwise.
     */
    @Override
    public boolean containsAll(Collection<? extends String> items) {
        for (String item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the hash table is empty.
     *
     * @return {@code true} if the table is empty, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes the specified item from the hash table if it is present.
     *
     * @param item The item to remove.
     * @return {@code true} if the item was removed successfully, {@code false} if the item was not found.
     */
    @Override
    public boolean remove(String item) {
        int index = Math.abs(functor.hash(item) % storage.length);
        if (storage[index].remove(item)) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * Removes all the items in the specified collection from the hash table.
     *
     * @param items The collection of items to remove.
     * @return {@code true} if at least one item was removed, {@code false} otherwise.
     */
    @Override
    public boolean removeAll(Collection<? extends String> items) {
        boolean modified = false;
        for (String item : items) {
            if (remove(item)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Returns the number of items currently stored in the hash table.
     *
     * @return The number of items in the table.
     */
    @Override
    public int size() {
        return size;
    }
}
