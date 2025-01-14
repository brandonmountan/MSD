package assignment06;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Timer_BST_Add_All_Random extends TimerTemplate {

    private ArrayList<Integer> dataset;
    private BinarySearchTree<Integer> bst;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public Timer_BST_Add_All_Random(int[] problemSizes, int timesToLoop) {
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

        Collections.shuffle(dataset);
    }

    /**
     * Measures the time for invoking the contains method on three specific elements.
     *
     * @param n Problem size.
     */
    @Override
    protected void timingIteration(int n) {
        // Insert items into the BST
        for (int item : dataset) {
            bst.add(item);
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
        int[] problemSizes = new int[21];
        problemSizes[0] = 0;
        for (int i = 1; i < 21; i++) {
            problemSizes[i] = problemSizes[i - 1] + 10000;
        }
        int timesToLoop = 10;

        try (FileWriter writer = new FileWriter("BST_Timing_Results_ADD_ALL_RANDOM.csv")) {

            // Run the Random BST experiment
            writer.write("Problem Size (N),BST ADD_ALL Time (ns)\n");
            Timer_BST_Add_All_Random randomAddALl = new Timer_BST_Add_All_Random(problemSizes, timesToLoop);
            TimerTemplate.Result[] randomAddAllResults = randomAddALl.run();

            // Write results to CSV
            for (int i = 0; i < problemSizes.length; i++) {
                writer.write(String.format("%d,%.2f\n",
                        randomAddAllResults[i].n(),
                        randomAddAllResults[i].avgNanoSecs()));
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

