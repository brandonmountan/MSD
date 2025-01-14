package assignment06;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Timer_BST_Sorted extends TimerTemplate {

    private ArrayList<Integer> dataset;
    private BinarySearchTree<Integer> bst;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public Timer_BST_Sorted(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
    }

    /**
     * Sets up the BST and dataset for the experiment based on the experiment type.
     *
     * @param n Problem size (number of elements in the dataset).
     */
    @Override
    protected void setup(int n) {
        dataset = new ArrayList<>();
        bst = new BinarySearchTree<>();

        // Generate dataset
        for (int i = 1; i <= n; i++) {
            dataset.add(i);
        }

        // Insert items into the BST
        for (int item : dataset) {
            bst.add(item);
        }
    }

    /**
     * Measures the time for invoking the contains method on three specific elements.
     *
     * @param n Problem size.
     */
    @Override
    protected void timingIteration(int n) {
        for (int item : dataset) {
            bst.contains(item);
        }
    }

    /**
     * Compensates for overhead by looping without interacting with the BST.
     *
     * @param n Problem size.
     */
    @Override
    protected void compensationIteration(int n) {
        for (int item : dataset) {
            int x = item;
        }
    }

    public static void main(String[] args) {
        int[] problemSizes = {10000, 20000, 30000, 40000, 45000};
        int timesToLoop = 5;

        try (FileWriter writer = new FileWriter("BST_Timing_Results_SORTED.csv")) {
//             Run the Sorted BST experiment
            writer.write("Problem Size (N),Sorted BST Time (ns)\n");
            Timer_BST_Sorted sortedRunner = new Timer_BST_Sorted(problemSizes, timesToLoop);
            TimerTemplate.Result[] sortedResults = sortedRunner.run();

            // Write results to CSV
            for (int i = 0; i < problemSizes.length; i++) {
                writer.write(String.format("%d,%.2f\n",
                        sortedResults[i].n(),
                        sortedResults[i].avgNanoSecs()));
            }


        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

