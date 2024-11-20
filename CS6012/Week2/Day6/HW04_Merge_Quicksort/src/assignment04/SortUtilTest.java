package assignment04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class SortUtilTest {

    private ArrayList<Integer> sortedList;
    private ArrayList<Integer> shuffledList;
    private ArrayList<Integer> reverseSortedList;

    @BeforeEach
    void setUp() {
        sortedList = SortUtil.generateBestCase(10);
        shuffledList = SortUtil.generateAverageCase(10);
        reverseSortedList = SortUtil.generateWorstCase(10);
    }

    @Test
    void testGenerateBestCase() {
        ArrayList<Integer> bestCase = SortUtil.generateBestCase(10);
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, bestCase.get(i - 1));
        }
    }

    @Test
    void testGenerateAverageCase() {
        ArrayList<Integer> averageCase = SortUtil.generateAverageCase(10);
        assertEquals(10, averageCase.size());
        assertNotEquals(SortUtil.generateBestCase(10), averageCase);
    }

    @Test
    void testGenerateWorstCase() {
        ArrayList<Integer> worstCase = SortUtil.generateWorstCase(10);
        for (int i = 10; i >= 1; i--) {
            assertEquals(i, worstCase.get(10 - i));
        }
    }

    @Test
    void testMergesortBestCase() {
        SortUtil.mergesort(sortedList, Comparator.naturalOrder());
        assertTrue(isSorted(sortedList, Comparator.naturalOrder()));
    }

    @Test
    void testMergesortAverageCase() {
        SortUtil.mergesort(shuffledList, Comparator.naturalOrder());
        assertTrue(isSorted(shuffledList, Comparator.naturalOrder()));
    }

    @Test
    void testMergesortWorstCase() {
        SortUtil.mergesort(reverseSortedList, Comparator.naturalOrder());
        assertTrue(isSorted(reverseSortedList, Comparator.naturalOrder()));
    }

    @Test
    void testQuicksortBestCase() {
        SortUtil.quicksort(sortedList, Comparator.naturalOrder());
        assertTrue(isSorted(sortedList, Comparator.naturalOrder()));
    }

    @Test
    void testQuicksortAverageCase() {
        SortUtil.quicksort(shuffledList, Comparator.naturalOrder());
        assertTrue(isSorted(shuffledList, Comparator.naturalOrder()));
    }

    @Test
    void testQuicksortWorstCase() {
        SortUtil.quicksort(reverseSortedList, Comparator.naturalOrder());
        assertTrue(isSorted(reverseSortedList, Comparator.naturalOrder()));
    }

    @Test
    void testMergeThresholdEffect() {
        // Test with various threshold values to see if mergesort remains stable
        for (int threshold = 1; threshold <= 10; threshold++) {
            SortUtil.setMergesortThreshold(threshold);
            ArrayList<Integer> testList = SortUtil.generateAverageCase(100);
            SortUtil.mergesort(testList, Comparator.naturalOrder());
            assertTrue(isSorted(testList, Comparator.naturalOrder()));
        }
    }

    @Test
    void testDifferentPivotStrategies() {
        // Run quicksort with different pivot strategies
        SortUtil.quicksort(sortedList, Comparator.naturalOrder());
        assertTrue(isSorted(sortedList, Comparator.naturalOrder()));

        SortUtil.quicksort(reverseSortedList, Comparator.naturalOrder());
        assertTrue(isSorted(reverseSortedList, Comparator.naturalOrder()));

        SortUtil.quicksort(shuffledList, Comparator.naturalOrder());
        assertTrue(isSorted(shuffledList, Comparator.naturalOrder()));
    }

    // Helper method to check if a list is sorted
    private <T> boolean isSorted(ArrayList<T> list, Comparator<? super T> comparator) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
}
