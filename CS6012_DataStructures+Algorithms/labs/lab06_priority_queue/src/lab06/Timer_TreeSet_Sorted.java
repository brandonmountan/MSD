package lab06;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Timer_TreeSet_Sorted extends TimerTemplate {

    private TreeSetPriorityQueue<Integer> priorityQueue;
    private ArrayList<Integer> testData;

    /**
     * Constructor for the TreeSetAddTimer.
     *
     * @param problemSizes the array of problem sizes to test
     * @param timesToLoop  the number of times to repeat each test
     */
    public Timer_TreeSet_Sorted(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }

    @Override
    protected void setup(int n) {
        priorityQueue = new TreeSetPriorityQueue<>();

        testData = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            testData.add(i);
        }
    }

    @Override
    protected void timingIteration(int n) {
        for (Integer value : testData) {
            priorityQueue.add(value);
        }
    }

    @Override
    protected void compensationIteration(int n) {
        // Simulate the iteration without doing any meaningful work
        for (Integer ignored : testData) {
            // Empty loop to match the iteration cost
        }
    }

    /**
     * Main method to run the timing experiment and write results to a CSV file.
     */
    public static void main(String[] args) {
        int[] problemSizes = new int[21];
        problemSizes[0] = 0;
        for (int i = 1; i < 21; i++) {
            problemSizes[i] = problemSizes[i - 1] + 10000;
        }
        int timesToLoop = 10;

        Timer_TreeSet_Loop timer = new Timer_TreeSet_Loop(problemSizes, timesToLoop);
        Result[] results = timer.run();

        // Write results to a CSV file
        String fileName = "treeset_add_timing_results_sorted.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write the header
            writer.append("Problem Size,Average Time (ns)\n");

            // Write each result
            for (Result result : results) {
                writer.append(result.n() + "," + result.avgNanoSecs() + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}
