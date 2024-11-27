package assignment07;

import java.math.BigInteger;
import java.util.Collection;

/**
 * A hash table of String objects that uses quadratic probing for collision resolution.
 */
public class QuadProbeHashTable implements Set<String> {
    private String[] table; // The hash table array
    private int size; // Number of elements in the table
    private final HashFunctor functor; // Hashing function
    private int capacity; // Current capacity of the table (always prime)

    private static final double LOAD_FACTOR_THRESHOLD = 0.5; // Max load factor before resizing

    /**
     * Constructs a hash table of the given capacity using the specified hash functor.
     * If the given capacity is not a prime number, the next largest prime is used.
     *
     * @param capacity Initial capacity (rounded to the next prime if not prime).
     * @param functor  HashFunctor to hash strings.
     * @throws IllegalArgumentException if the capacity is less than or equal to 0.
     */
    public QuadProbeHashTable(int capacity, HashFunctor functor) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        this.capacity = nextPrime(capacity);
        this.table = new String[this.capacity];
        this.functor = functor;
        this.size = 0;
    }

    /**
     * Adds the specified item to the hash table if it is not already present.
     * Resizes the table if the load factor exceeds the threshold.
     *
     * @param item The item to add.
     * @return {@code true} if the item was added successfully, {@code false} if it was already present.
     * @throws IllegalArgumentException if the item is {@code null}.
     */
    @Override
    public boolean add(String item) {
        if (item == null) {
            throw new IllegalArgumentException("Null values are not allowed in the hash table.");
        }
        if (contains(item)) {
            return false; // Prevent duplicates
        }
        if ((double) size / capacity > LOAD_FACTOR_THRESHOLD) {
            resize(); // Resize and rehash when load factor exceeds threshold
        }
        int index = findIndex(item);
        table[index] = item; // Insert the item at the found index
        size++;
        return true;
    }

    /**
     * Adds all items from the specified collection to the hash table.
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
     * Checks whether the specified item is present in the hash table.
     *
     * @param item The item to search for.
     * @return {@code true} if the item is found, {@code false} otherwise.
     */
    @Override
    public boolean contains(String item) {
        if (item == null) {
            return false;
        }
        int index = findIndex(item);
        return table[index] != null && table[index].equals(item);
    }

    /**
     * Checks whether all items in the specified collection are present in the hash table.
     *
     * @param items The collection of items to search for.
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
     * Removes the specified item from the hash table if it is present.
     *
     * @param item The item to remove.
     * @return {@code true} if the item was removed successfully, {@code false} if the item was not found.
     */
    @Override
    public boolean remove(String item) {
        if (item == null) {
            return false;
        }
        int index = findIndex(item);
        if (table[index] != null && table[index].equals(item)) {
            table[index] = null; // Mark as removed
            size--;
            return true;
        }
        return false;
    }

    /**
     * Removes all items in the specified collection from the hash table.
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
     * Clears all items from the hash table.
     */
    @Override
    public void clear() {
        table = new String[capacity];
        size = 0;
    }

    /**
     * Checks whether the hash table is empty.
     *
     * @return {@code true} if the hash table is empty, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the hash table.
     *
     * @return The current size of the hash table.
     */
    @Override
    public int size() {
        return size;
    }

    // --- Helper Methods ---

    /**
     * Finds the index for the given item using quadratic probing.
     *
     * @param item The item to find the index for.
     * @return The index where the item should be inserted or is located.
     */
    private int findIndex(String item) {
        int hash = Math.abs(functor.hash(item) % capacity);
        int index = hash;
        int i = 1;
        while (table[index] != null && !table[index].equals(item)) {
            index = (hash + i * i) % capacity; // Quadratic probing formula
            i++;
        }
        return index;
    }

    /**
     * Resizes the table to the next largest prime greater than twice the current capacity.
     * Rehashes all existing elements into the new table.
     */
    private void resize() {
        int newCapacity = nextPrime(2 * capacity);
        String[] oldTable = table;
        table = new String[newCapacity];
        capacity = newCapacity;
        size = 0; // Reset size and rehash all elements
        for (String item : oldTable) {
            if (item != null) {
                add(item); // Rehash each non-null item
            }
        }
    }

    /**
     * Finds the next largest prime number greater than or equal to the given number.
     *
     * @param n The number to start from.
     * @return The next prime number.
     */
    private int nextPrime(int n) {
        BigInteger bigInt = BigInteger.valueOf(n);
        return bigInt.isProbablePrime(100) ? n : bigInt.nextProbablePrime().intValue();
    }
}
