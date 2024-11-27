package assignment03;

import java.util.SortedSet;
import java.util.TreeSet;

public class AddTiming extends TimerTemplate {
    SortedSet<Integer> sortedSet;
    int targetElement;

    /**
     * Constructor that sets up the timer with problem sizes and the number of times to loop.
     *
     * @param problemSizes Array of sizes of the sorted set (from 2^10 to 2^20).
     * @param timesToLoop  Number of times to repeat the tests for averaging.
     */
    public AddTiming(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }

    @Override
    protected void setup(int n) {
        // Initialize the sorted set with n elements
        sortedSet = new TreeSet<>();
        for (int i = 0; i < n; i++) {
            sortedSet.add(i);
        }
        targetElement = n; // Set a target element to add consistently across tests
    }

    @Override
    protected void timingIteration(int n) {
        sortedSet.add(targetElement);  // Time only the add method
        sortedSet.remove(targetElement); // Remove to keep the set size consistent
    }

    @Override
    protected void compensationIteration(int n) {
        // Mimic any setup costs (but without calling add on the actual target element)
        sortedSet.contains(targetElement); // Call contains instead, which doesn't modify the set
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[12];
        problemSizes[0] = 1024;
        for (int i = 1; i < 12; i++) {
            problemSizes[i] = (int) Math.pow(2, 10 + (i - 1));  // Sizes from 2^10 to 2^20
        }
        AddTiming timing = new AddTiming(problemSizes, 10000);  // 10,000 repetitions for averaging
        TimerTemplate.Result[] results = timing.run();

        for (TimerTemplate.Result result : results) {
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
