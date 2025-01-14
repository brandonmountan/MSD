package assignment07;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashPerformanceTimingADD extends TimerTemplate {
    // instance to be tested
    private static ChainingHashTable table;
    // list of string values to be added to hash table for performance evaluation
    private List<String> data;
    private HashFunctor functor;

    public HashPerformanceTimingADD(int[] problemSizes, int timesToLoop, HashFunctor functor) {
        super(problemSizes, timesToLoop);
        this.functor = functor;
    }

    @Override
    // prepare environment ofr timing test with problem size n.
    protected void setup(int n) {
        // create new hash table with capacity 10 times problem size to reduce collisions
        table = new ChainingHashTable(n * 10, functor);
        // initialize data list to hold strings to be inserted
        data = new ArrayList<>();
        // populate data list with n unique strings ("String0", "String1")
        for (int i = 0; i < n; i++) {
            data.add("String" + i);
        }
    }

    @Override
    // timing adding strings to hash table
    protected void timingIteration(int n) {
        for (String s : data) {
            table.add(s);
        }
    }

    @Override
    // implement compensation phase to measure baseline overhead of iterating over data
    // purpose: allow framework to subtract this baseline overhead from timing results
    protected void compensationIteration(int n) {
        // loop through data but don't perform any operations
        for (String s : data) {
            // Perform a no-op to simulate the overhead
        }
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[11];
        problemSizes[0] = 10000;
        for (int i = 1; i < 11; i++) {
            problemSizes[i] = problemSizes[i - 1] + 10000;
        }
        int timesToLoop = 10;
        String outputFileName = "hash_performance_results_ADD.csv";

        // Use a StringBuilder to accumulate CSV output.
        // collects CSV data for writing to a file
        StringBuilder csvOutput = new StringBuilder();
        csvOutput.append("HashFunctor,ProblemSize,AverageTimeNS,Collisions\n");

        // iterate over three hash functors
        // create HashPerformanceTiming instance for current hash functor
        for (HashFunctor functor : List.of(new BadHashFunctor(), new MediocreHashFunctor(), new GoodHashFunctor())) {
            HashPerformanceTimingADD timing = new HashPerformanceTimingADD(problemSizes, timesToLoop, functor);
            var results = timing.run();

            // add header row
            for (Result result : results) {
                // Append results to the CSV
                csvOutput.append(functor.getClass().getSimpleName())
                        .append(",")
                        .append(result.n())
                        .append(",")
                        .append(result.avgNanoSecs())
                        .append("\n");
            }
        }

        // Write the CSV data to a file
        // try with resources ensures FileWriter is automatically closed after use
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(csvOutput.toString());
            System.out.println("Results written to " + outputFileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
