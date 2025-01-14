package assignment04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SortUtil {
    private static int mergesortThreshold = 10; // Default threshold for mergesort
    private static int pivotStrategy = 2; // Default pivot strategy for quicksort

    // Constants for pivot selection strategies
    public static final int FIRST_ELEMENT = 0;
    public static final int RANDOM_ELEMENT = 1;
    public static final int MEDIAN_OF_THREE = 2;

    /**
     * Sets the threshold value for mergesort to switch to insertion sort.
     */
    public static void setMergesortThreshold(int threshold) {
        mergesortThreshold = threshold;
    }

    /**
     * Sets the pivot selection strategy for quicksort.
     */
    public static void setPivotStrategy(int strategy) {
        pivotStrategy = strategy;
    }

    /**
     * Mergesort implementation with threshold for switching to insertion sort.
     */
    public static <T> void mergesort(ArrayList<T> list, Comparator<? super T> cmp) {
        if (list.size() <= mergesortThreshold) {
            insertionSort(list, cmp);
        } else {
            ArrayList<T> aux = new ArrayList<>(list);
            mergesortHelper(list, aux, 0, list.size() - 1, cmp);
        }
    }

    private static <T> void mergesortHelper(ArrayList<T> list, ArrayList<T> aux, int low, int high, Comparator<? super T> cmp) {
        if (low < high) {
            int mid = (low + high) / 2;
            mergesortHelper(aux, list, low, mid, cmp);
            mergesortHelper(aux, list, mid + 1, high, cmp);
            merge(list, aux, low, mid, high, cmp);
        }
    }

    private static <T> void merge(ArrayList<T> list, ArrayList<T> aux, int low, int mid, int high, Comparator<? super T> cmp) {
        int i = low, j = mid + 1;
        for (int k = low; k <= high; k++) {
            if (i > mid) {
                list.set(k, aux.get(j++));
            } else if (j > high) {
                list.set(k, aux.get(i++));
            } else if (cmp.compare(aux.get(j), aux.get(i)) < 0) {
                list.set(k, aux.get(j++));
            } else {
                list.set(k, aux.get(i++));
            }
        }
    }

    /**
     * Quicksort implementation with configurable pivot selection strategy.
     */
    public static <T> void quicksort(ArrayList<T> list, Comparator<? super T> cmp) {
        quicksortHelper(list, 0, list.size() - 1, cmp);
    }

    private static <T> void quicksortHelper(ArrayList<T> list, int low, int high, Comparator<? super T> cmp) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, cmp);
            quicksortHelper(list, low, pivotIndex - 1, cmp);
            quicksortHelper(list, pivotIndex + 1, high, cmp);
        }
    }

    private static <T> int partition(ArrayList<T> list, int low, int high, Comparator<? super T> cmp) {
        T pivot = selectPivot(list, low, high, cmp);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> T selectPivot(ArrayList<T> list, int low, int high, Comparator<? super T> cmp) {
        switch (pivotStrategy) {
            case FIRST_ELEMENT:
                return list.get(low);
            case RANDOM_ELEMENT:
                return list.get(new Random().nextInt(high - low + 1) + low);
            case MEDIAN_OF_THREE:
                int mid = (low + high) / 2;
                return medianOfThree(list.get(low), list.get(mid), list.get(high), cmp);
            default:
                throw new IllegalArgumentException("Invalid pivot strategy");
        }
    }

    private static <T> T medianOfThree(T a, T b, T c, Comparator<? super T> cmp) {
        if (cmp.compare(a, b) < 0) {
            if (cmp.compare(b, c) < 0) return b;
            return cmp.compare(a, c) < 0 ? c : a;
        } else {
            if (cmp.compare(a, c) < 0) return a;
            return cmp.compare(b, c) < 0 ? c : b;
        }
    }

    private static <T> void insertionSort(ArrayList<T> list, Comparator<? super T> cmp) {
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    /**
     * Generates a best-case (already sorted) list.
     */
    public static ArrayList<Integer> generateBestCase(int size) {
        ArrayList<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    /**
     * Generates an average-case (random) list.
     */
    public static ArrayList<Integer> generateAverageCase(int size) {
        ArrayList<Integer> list = generateBestCase(size);
        Collections.shuffle(list, new Random(0)); // Use fixed seed for consistency
        return list;
    }

    /**
     * Generates a worst-case (reverse sorted) list.
     */
    public static ArrayList<Integer> generateWorstCase(int size) {
        ArrayList<Integer> list = generateBestCase(size);
        Collections.reverse(list);
        return list;
    }
}
