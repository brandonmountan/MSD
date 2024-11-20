package assignment04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class QuickSortPivotTimer extends TimerTemplate {
    private final int pivotStrategy;
    private ArrayList<Integer> list;

    public QuickSortPivotTimer(int[] problemSizes, int timesToLoop, int pivotStrategy) {
        super(problemSizes, timesToLoop);
        this.pivotStrategy = pivotStrategy;
    }

    @Override
    protected void setup(int n) {
        list = SortUtil.generateAverageCase(n);
        SortUtil.setPivotStrategy(pivotStrategy); // Set the pivot strategy for testing
    }

    @Override
    protected void timingIteration(int n) {
        ArrayList<Integer> tempList = new ArrayList<>(list);
        SortUtil.quicksort(tempList, Comparator.naturalOrder());
    }

    @Override
    protected void compensationIteration(int n) {
        new ArrayList<>(list); // Compensate for copying time
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[11];
        for (int i = 0; i <= 10; i++) {
            problemSizes[i] = (int) Math.pow(2, 7 + i);
        }

        int timesToLoop = 5;
        int[] pivotStrategies = {SortUtil.FIRST_ELEMENT, SortUtil.RANDOM_ELEMENT, SortUtil.MEDIAN_OF_THREE};

        for (int pivotStrategy : pivotStrategies) {
            System.out.println("Timing QuickSort with pivot strategy = " + pivotStrategy);
            QuickSortPivotTimer timer = new QuickSortPivotTimer(problemSizes, timesToLoop, pivotStrategy);
            TimerTemplate.Result[] results = timer.run();

            for (TimerTemplate.Result result : results) {
                System.out.printf("%d, %.2f\n", result.n(), result.avgNanoSecs());
            }
            System.out.println();
        }
    }
}
