package assignment04;

import java.util.ArrayList;
import java.util.Comparator;

public class MergesortThresholdTimer2 extends TimerTemplate {
    private final int threshold;
    private ArrayList<Integer> originalList; // Stores the original list

    public MergesortThresholdTimer2(int timesToLoop, int threshold) {
        super(new int[]{1}, timesToLoop); // Fixed size array, problem size is constant
        this.threshold = threshold;
    }

    @Override
    protected void setup(int n) {
        // Generate the original list once, ensuring it's consistent across iterations
        originalList = SortUtil.generateAverageCase((int) Math.pow(2, 22));
        SortUtil.setMergesortThreshold(threshold); // Set the threshold for this run
    }

    @Override
    protected void timingIteration(int n) {
        // Clone the original list to ensure it's unmodified
        ArrayList<Integer> tempList = new ArrayList<>(originalList);
        SortUtil.mergesort(tempList, Comparator.naturalOrder());
    }

    @Override
    protected void compensationIteration(int n) {
        // Compensate for the time it takes to clone the list
        new ArrayList<>(originalList);
    }

    public static void main(String[] args) {
        int timesToLoop = 10; // Number of repetitions for each threshold
        System.out.println("Timing mergesort for a fixed array size (2^25) with varying thresholds:");
        System.out.printf("%-10s %-10s\n", "Threshold", "Time (ms)");

        for (int threshold = 0; threshold <= 200; threshold += 10) {
            MergesortThresholdTimer2 timer = new MergesortThresholdTimer2(timesToLoop, threshold);
            TimerTemplate.Result[] results = timer.run();

            // Calculate the average timing for the single fixed problem size
            double averageTime = results[0].avgNanoSecs() / 1_000_000.0; // Convert nanoseconds to milliseconds
            System.out.printf("%-10d %-10.2f\n", threshold, averageTime);
        }
    }
}
