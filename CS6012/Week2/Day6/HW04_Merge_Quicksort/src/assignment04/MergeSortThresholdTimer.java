package assignment04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class MergeSortThresholdTimer extends TimerTemplate {
    private final int threshold;
    private ArrayList<Integer> list;

    public MergeSortThresholdTimer(int[] problemSizes, int timesToLoop, int threshold) {
        super(problemSizes, timesToLoop);
        this.threshold = threshold;
    }

    @Override
    protected void setup(int n) {
        list = SortUtil.generateAverageCase(n);
        SortUtil.setMergesortThreshold(threshold); // Set the threshold value for testing
    }

    @Override
    protected void timingIteration(int n) {
        ArrayList<Integer> tempList = new ArrayList<>(list);
        SortUtil.mergesort(tempList, Comparator.naturalOrder());
    }

    @Override
    protected void compensationIteration(int n) {
        new ArrayList<>(list); // Compensate for copying time
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[11];
        for (int i = 0; i <= 10; i++) {
            problemSizes[i] = (int) Math.pow(2, 15 + i);
        }

        int timesToLoop = 5;
        int[] thresholds = {0, 10, 20, 30, 40, 50, 60, 10000};

        for (int threshold : thresholds) {
            System.out.println("Timing MergeSort with threshold = " + threshold);
            MergeSortThresholdTimer timer = new MergeSortThresholdTimer(problemSizes, timesToLoop, threshold);
            TimerTemplate.Result[] results = timer.run();

            for (TimerTemplate.Result result : results) {
                System.out.printf("%d, %.2f\n", result.n(), result.avgNanoSecs());
            }
            System.out.println();
        }
    }
}
