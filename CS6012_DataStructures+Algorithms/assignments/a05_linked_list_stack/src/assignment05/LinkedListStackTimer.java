package assignment05;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LinkedListStackTimer extends TimerTemplate {

    private LinkedListStack<Integer> stack;

    public LinkedListStackTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }

    @Override
    protected void setup(int n) {
        stack = new LinkedListStack<>();
        // Pre-fill the stack with n elements
        for (int i = 0; i < n; i++) {
            stack.push(i);
        }
    }

    @Override
    protected void timingIteration(int n) {
        // Perform push and pop for timing
//        stack.push(1);
//        stack.pop();
        stack.peek();

    }

    @Override
    protected void compensationIteration(int n) {
    }

    public static void main(String[] args) {
        // Generate problem sizes from 2^10 to 2^20
        int[] problemSizes = new int[14];
        problemSizes[0] = 1024;
        for (int i = 1; i <= 13; i++) {
            problemSizes[i] = (int) Math.pow(2, 10 + i);
        }
        int timesToLoop = 1000;
        LinkedListStackTimer timer = new LinkedListStackTimer(problemSizes, timesToLoop);

        var results = timer.run();

        // Write results to a CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter("LinkedListStackTimerResultsPEEK.csv"))) {
            writer.println("ProblemSize,AvgTime(ns)");
            for (Result result : results) {
                writer.printf("%d,%.2f\n", result.n(), result.avgNanoSecs());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        System.out.println("Results written to LinkedListStackTimerResultsPEEK.csv");
    }
}
