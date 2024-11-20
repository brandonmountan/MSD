package assignment04;

import java.util.ArrayList;
import java.util.Comparator;

public class MergeSortVsQuickSortTimer extends TimerTemplate {
    private final String algorithm;
    private final String caseType;
    private ArrayList<Integer> list;

    public MergeSortVsQuickSortTimer(int[] problemSizes, int timesToLoop, String algorithm, String caseType) {
        super(problemSizes, timesToLoop);
        this.algorithm = algorithm;
        this.caseType = caseType;
    }

    @Override
    protected void setup(int n) {
        switch (caseType) {
            case "best" -> list = SortUtil.generateBestCase(n);
            case "average" -> list = SortUtil.generateAverageCase(n);
            case "worst" -> list = SortUtil.generateWorstCase(n);
        }
    }

    @Override
    protected void timingIteration(int n) {
        ArrayList<Integer> tempList = new ArrayList<>(list);
        if ("mergesort".equals(algorithm)) {
            SortUtil.mergesort(tempList, Comparator.naturalOrder());
        } else if ("quicksort".equals(algorithm)) {
            SortUtil.quicksort(tempList, Comparator.naturalOrder());
        }
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

        int timesToLoop = 2;
        String[] algorithms = {"mergesort", "quicksort"};
        String[] cases = {"best", "average", "worst"};

        for (String algorithm : algorithms) {
            for (String caseType : cases) {
                System.out.println("Timing " + algorithm + " on " + caseType + " case");
                MergeSortVsQuickSortTimer timer = new MergeSortVsQuickSortTimer(problemSizes, timesToLoop, algorithm, caseType);
                TimerTemplate.Result[] results = timer.run();

                for (TimerTemplate.Result result : results) {
                    System.out.printf("%d, %.2f\n", result.n(), result.avgNanoSecs());
                }
                System.out.println();
            }
        }
    }
}
