package assignment07;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashPerformanceTimingREMOVE extends TimerTemplate {
    // Instance to be tested
    private ChainingHashTable table;
    // List of string values to be tested for "remove" performance
    private List<String> data;
    private HashFunctor functor;

    public HashPerformanceTimingREMOVE(int[] problemSizes, int timesToLoop, HashFunctor functor) {
        super(problemSizes, timesToLoop);
        this.functor = functor;
    }

    @Override
    // Prepare the environment for the timing test with problem size n
    protected void setup(int n) {
        // Create a new hash table with capacity 10 times problem size to reduce collisions
        table = new ChainingHashTable(n * 10, functor);
        // Initialize data list to hold strings to be inserted and then removed
        data = new ArrayList<>();
        // Populate data list with n unique strings ("String0", "String1", ...)
        for (int i = 0; i < n; i++) {
            String str = "String" + i;
            data.add(str);
            table.add(str); // Pre-load the hash table with the data
        }
    }

    @Override
    // Time the "remove" method for the hash table
    protected void timingIteration(int n) {
        for (String s : data) {
            table.remove(s); // Measure the time it takes to remove elements
        }
    }

    @Override
    // Implement compensation phase to measure baseline overhead of iterating over data
    protected void compensationIteration(int n) {
        // Loop through data but don't perform any operations
        for (String s : data) {
            // Perform a no-op to simulate the overhead
        }
    }

    public static void main(String[] args) {
        // Define problem sizes for testing
        int[] problemSizes = new int[11];
        problemSizes[0] = 10000;
        for (int i = 1; i < 11; i++) {
            problemSizes[i] = problemSizes[i - 1] + 10000; // Increment sizes by 1000
        }
        int timesToLoop = 10;
        String outputFileName = "hash_performance_results_REMOVE.csv";

        // Use a StringBuilder to accumulate CSV output
        StringBuilder csvOutput = new StringBuilder();
        csvOutput.append("HashFunctor,ProblemSize,AverageTimeNS\n");

        // Iterate over three hash functors
        for (HashFunctor functor : List.of(new BadHashFunctor(), new MediocreHashFunctor(), new GoodHashFunctor())) {
            // Create a timing instance for the current hash functor
            HashPerformanceTimingREMOVE timing = new HashPerformanceTimingREMOVE(problemSizes, timesToLoop, functor);
            var results = timing.run();

            // Add results to the CSV
            for (Result result : results) {
                csvOutput.append(functor.getClass().getSimpleName())
                        .append(",")
                        .append(result.n())
                        .append(",")
                        .append(result.avgNanoSecs())
                        .append("\n");
            }
        }

        // Write the CSV data to a file
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(csvOutput.toString());
            System.out.println("Results written to " + outputFileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
