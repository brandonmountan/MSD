import java.util.Arrays;
import java.util.Comparator;

public class Median {

    /**
     * Get the median element in the array
     *
     * @param array
     * @param <T>
     * @return the median element
     */
    public static <T extends Comparable<T>> T getMedian(T[] array) {
        Arrays.sort(array);
        return array[array.length / 2];
    }

    /**
     * Get median element in the array sorted by the given comparator
     *
     * @param array
     * @param comparator
     * @return the median element
     * @param <T>
     */
    public static <T> T getMedian(T[] array, Comparator<T> comparator) {
        Arrays.sort(array, comparator);
        return array[array.length / 2];
    }
}
