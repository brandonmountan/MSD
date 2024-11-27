package assignment07;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A hash table that uses separate chaining to resolve collisions.
 */
public class ChainingHashTable implements Set<String> {
    // array where each index stores a bucket of strings
    // collisions handled by adding colliding elements into same bucket (linked list)
    private LinkedList<String>[] storage;
    // used to compute hash values for strings
    private HashFunctor functor;
    // number of elements currently stored in hash table
    private int size;
    // track number of collisions
    private int collisions;

    /**
     * Constructs a ChainingHashTable with a specified capacity and hash functor.
     * @param capacity The initial capacity of the table.
     * @param functor The hash function to use.
     */
    // suppress warning about unchecked typecasting (from Object to LinkedList<String>).
    // Neccessary due to how Java generics work with arrays
    @SuppressWarnings("unchecked")
    public ChainingHashTable(int capacity, HashFunctor functor) {
        // creates an array with specified number of buckets (capacity)
        // does not initialize linked lists yet
        this.storage = (LinkedList<String>[]) new LinkedList[capacity];
        this.functor = functor;
        // table is empty is at start
        this.size = 0;
        this.collisions = 0;
    }

    @Override
    public boolean add(String item) {
        // compute hash value of item using functor. Takes its modulus to map it into the storage array
        // ensures result is non-negative
        int index = Math.abs(functor.hash(item) % storage.length);
        // check if item already exists in corresponding bucket to prevent duplicates
        if (storage[index] == null) {
            storage[index] = new LinkedList<>();
            // add item to bucket at computed index
            storage[index].add(item);
        } else if (storage[index].contains(item)) {
            return false;
        } else {
            storage[index].add(item);
            collisions++;
        }
        size++;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> items) {
        // track whether any items were added
        boolean modified = false;
        // iterate through collection of items
        for (String item : items) {
            // add each item and update modified if any new elements were added
            if (add(item)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        // clear each linked list (bucket) in the storage array
        for (LinkedList<String> bucket : storage) {
            bucket.clear();
        }
        // reset size to zero
        size = 0;
    }

    @Override
    public boolean contains(String item) {
        // compute bucket index for item
        int index = Math.abs(functor.hash(item) % storage.length);
        // check if bucket contains item
        return storage[index].contains(item);
    }

    @Override
    public boolean containsAll(Collection<? extends String> items) {
        // iterate through all items in collection
        for (String item : items) {
            // return false if any item is not in the hash table
            if (!contains(item)) {
                return false;
            }
        }
        // return true if all items are found in the hash table
        return true;
    }

    @Override
    public boolean isEmpty() {
        // return true if table contains no elements
        return size == 0;
    }

    @Override
    public boolean remove(String item) {
        // compute bucket index for item
        int index = Math.abs(functor.hash(item) % storage.length);
        // remove item from bucket and decrement size if successful
        if (storage[index].remove(item)) {
            size--;
            return true;
        }
        return false;
    }

    @Override
    // similar to addAll. Iterates through collection and removes each item. returns true if any item was removed
    public boolean removeAll(Collection<? extends String> items) {
        boolean modified = false;
        for (String item : items) {
            if (remove(item)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public int size() {
        return size;
    }

    public int getCollisions() {
        return collisions;
    }

    protected void resetCollisions() {
        collisions = 0;
    }

    public int getLambda() {
        int maxLamda = storage[0].size();
        for (int i = 1; i < storage.length; i++) {
            if (storage[i].size() > maxLamda) {
                maxLamda = storage[i].size();
            }
        }
        return maxLamda;
    }
}
