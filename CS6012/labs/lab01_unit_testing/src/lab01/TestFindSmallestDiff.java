package lab01;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Required for @BeforeAll and @AfterAll in JUnit 5
public class TestFindSmallestDiff {
    private int[] arr1, arr2, arr3, arr4, arr5, arr6;

    @BeforeAll
    public void setUpBeforeClass() {
        System.out.println("Setting up before all tests run.");
    }

    @BeforeEach
    public void setUp() {
        // Initialize test arrays before each test
        arr1 = new int[0];                        // Empty array
        arr2 = new int[] { 3, 3, 3 };             // Identical elements
        arr3 = new int[] { 52, 4, -8, 0, -17 };   // Small random array
        arr4 = new int[] { 10, -10 };             // Only two elements
        arr5 = new int[] { -7, -3, 2, 5, 9 };     // Positive and negative elements
        arr6 = new int[] { 1, 2, 3, 4 };          // Consecutive integers
    }

    @Test
    public void testEmptyArray() {
        assertEquals(-1, DiffUtil.findSmallestDiff(arr1), "Expected -1 for an empty array.");
    }

    @Test
    public void testAllArrayElementsEqual() {
        assertEquals(0, DiffUtil.findSmallestDiff(arr2), "Expected 0 when all elements are identical.");
    }

    @Test
    public void testSmallRandomArrayElements() {
        assertEquals(4, DiffUtil.findSmallestDiff(arr3), "Expected 4 for the smallest difference in array [52, 4, -8, 0, -17].");
    }

    @Test
    public void testArrayWithTwoElements() {
        assertEquals(20, DiffUtil.findSmallestDiff(arr4), "Expected 20 for the difference between 10 and -10.");
    }

    @Test
    public void testArrayWithPositiveAndNegativeElements() {
        assertEquals(3, DiffUtil.findSmallestDiff(arr5), "Expected 3 for the smallest difference in array [-7, -3, 2, 5, 9].");
    }

    @Test
    public void testConsecutiveIntegers() {
        assertEquals(1, DiffUtil.findSmallestDiff(arr6), "Expected 1 for consecutive integers.");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Tearing down after a test.");
    }

    @AfterAll
    public void tearDownAfterClass() {
        System.out.println("All tests completed.");
    }
}
