package assignment03;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinarySearchSet<E> implements SortedSet<E>, Iterable<E> {
    // cannot create an array of a generic type with new E[size] so need to create array of type Object and case to E[]
    private E[] array;
    // size of input
    private int size;
    // comparator to order elements in the specified way
    private Comparator<? super E> comparator;

    // used to create SortedSet
    // assumed elements are ordered using their natural ordering, which means we need compareTo (from Comparable) in helper method.
    // O(1)
    @SuppressWarnings("unchecked")
    public BinarySearchSet() {
        array = (E[]) new Object[10];
        size = 0;
        // Set will rely on compareTo method
        comparator = null;
    }

    // elements ordered by provided comparator so this constructor creates the sorted set.
    // O(1)
    @SuppressWarnings("unchecked")
    // The '? super E' wildcard provides flexibility, adaptability, versatility...
    // for example, if BinarySearchSet<Number>, then Comparator<? super Number> can compare more specific types like Integer or Double
    public BinarySearchSet(Comparator<? super E> comparator) {
        array = (E[]) new Object[10];
        size = 0;
        this.comparator = comparator;
    }

    // need to cast E to Comparable<E> in order to call compareTo
    // O(1)
    private int compare(E a, E b) {
        if (comparator == null) {
            // if no comparator is provided, it assumes natural ordering and casts E to Comparable<E>
            return ((Comparable<? super E>) a).compareTo(b);
        } else {
            return comparator.compare(a, b);
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public E first() {
        if (size == 0) {
            throw new NoSuchElementException("Set is empty.");
        }
        return array[0];
    }

    @Override
    public E last() {
        if (size == 0) {
            throw new NoSuchElementException("Set is empty.");
        }
        return array[size - 1];
    }

    // false return means null or element already exists in set
    @Override
    public boolean add(E element) {
        if (element == null) {
            return false;
        }
        // binarySearch will return index if element is present in array
        int index = binarySearch(element);
        if (index >= 0) {
            return false; // element is already in set
        }
        // if element not in array, binarySearch will return negative value that represents insertion point (-insertionPoint - 1)
        // calculate insertion point by reversing (index = -index - 1)
        index = -index - 1;
        if (size == array.length) {
            resizeArray();
        }
        // shift elements in the array one position to the right, starting from the index where element should be inserted
        // array is source array (and destination array), index is starting position in source array, index + 1 is starting position in destination array, size - index is number of elements to shift
        // ultimately, makes room for new element
        System.arraycopy(array, index, array, index + 1, size - index);
        // new element
        array[index] = element;
        size++;
        return true;
    }

    // add all elements from given collection to this set.
    // ? = wildcard (unknown type) , extends E = restricts type to E or subclass of E
    @Override
    public boolean addAll(Collection<? extends E> elements) {
        // declare boolean and set it to false
        boolean modified = false;
        // iterates over each element in Collection elements of type E
        for (E element : elements) {
            // modified = modified | add(element);
            // if add(element) returns true, modified will return true
            // false means element was already in array and not added
            // operator says modified will be true if at least one element was successfully added, even if other elements were already in array
            modified |= add(element);
        }
        return modified;
    }

    @Override
    public void clear() {
        size = 0;
    }

    // O(log N)
    // just checks if element is in set
    @Override
    public boolean contains(E element) {
        return binarySearch(element) >= 0;
    }

    // O(M log N) where M is number of elements in Collection
    @Override
    public boolean containsAll(Collection<? extends E> elements) {
        for (E element : elements) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new BinarySearchSetIterator();
    }

    // opposite of add
    @Override
    public boolean remove(E element) {
        int index = binarySearch(element);
        if (index < 0) {
            return false;
        }
        System.arraycopy(array, index + 1, array, index, size - index - 1);
        size--;
        array[size] = null;  // Avoid memory leaks
        return true;
    }

    // opposite of addAll
    @Override
    public boolean removeAll(Collection<? extends E> elements) {
        boolean modified = false;
        for (E element : elements) {
            modified |= remove(element);
        }
        return modified;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E[] toArray() {
        E[] result = (E[]) new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = array[i];
        }
        return result;
    }

    // Efficiency O(log N)
    private int binarySearch(E element) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            // same as (low + high) / 2 except it avoids potential overflow issues.
            // unsigned right shift
            int mid = (low + high) >>> 1;
            int cmp = compare(element, array[mid]);
            // element less than array[mid]
            if (cmp < 0) {
                high = mid - 1;
            // element greater than array[mid]
            } else if (cmp > 0) {
                low = mid + 1;
            // 0 if element = array[mid]. Return index where element was found
            } else {
                return mid;
            }
        }
        // if element is not found, return negative value representing insertion point
        return -(low + 1);
    }

    @SuppressWarnings("unchecked")
    private void resizeArray() {
        E[] newArray = (E[]) new Object[array.length * 2];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
        }
        array = newArray;
    }

    // need a way to retrieve next or remove elements in a controlled, sequential manner
    private class BinarySearchSetIterator implements Iterator<E> {
        private int currentIndex = 0;
        private boolean canRemove = false;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the set.");
            }
            canRemove = true;
            return array[currentIndex++];
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("next() has not been called or remove() has already been called.");
            }
            BinarySearchSet.this.remove(array[--currentIndex]);
            canRemove = false;
        }
    }
}
