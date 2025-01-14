package lab03;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;

public class ContainsTiming extends TimerTemplate {
    SortedSet<Integer> sortedSet = new TreeSet<>();
    int targetElement;
    // sizes range from 2^10 to 2^20
//    int[] sizes = {1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576};
//    int length = sizes.length;
//    int runContainsMethod = 10000;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public ContainsTiming(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }


    @Override
    protected void setup(int n) {
        sortedSet = new TreeSet<>();
        for (int i = 0; i < n; i++) {
            sortedSet.add(i);
        }
        targetElement = n - 1; // Set target element to last one for consistency in testing
    }

    @Override
    protected void timingIteration(int n) {
        sortedSet.contains(targetElement); // time only the contains method
    }

    @Override
    protected void compensationIteration(int n) {
        // perform no operation for compensation, as contains has no setup or teardown costs
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[11];
        for (int i = 0; i < 11; i++) {
            problemSizes[i] = (int) Math.pow(2, 10 + i);  // Sizes from 2^10 to 2^20
        }
        ContainsTiming timing = new ContainsTiming(problemSizes, 10000);  // 10,000 repetitions for averaging
        TimerTemplate.Result[] results = timing.run();

        for (TimerTemplate.Result result : results) {
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }

}
