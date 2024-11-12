import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class MedianTest {
//    List<Integer> numList;
    Integer[] numArray;
    Comparator<Integer> comparator;

    @BeforeEach
    void setUp() {
        numArray = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Test
    void getMedian1() {
        assertEquals(6, Median.getMedian(numArray));
    }

    @Test
    void getMedian2() {
        assertEquals(6, Median.getMedian(numArray, comparator));
    }
}
