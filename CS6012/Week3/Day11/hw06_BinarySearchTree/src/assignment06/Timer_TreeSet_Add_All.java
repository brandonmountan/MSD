package assignment06;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class Timer_TreeSet_Add_All extends TimerTemplate {

    private ArrayList<Integer> dataset;
    private BinarySearchTree<Integer> bst;
    private TreeSet<Integer> treeSet;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public Timer_TreeSet_Add_All(int[] problemSizes, int timesToLoop) {
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
        treeSet = new TreeSet<>();
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
        // Insert items into the tree set
        treeSet.addAll(dataset);
    }

    /**
     *
     * @param n Problem size.
     */
    @Override
    protected void compensationIteration(int n) {

    }

    public static void main(String[] args) {
        int[] problemSizes = new int[21];
        problemSizes[0] = 0;
        for (int i = 1; i < 21; i++) {
            problemSizes[i] = problemSizes[i - 1] + 10000;
        }
        int timesToLoop = 10;

        try (FileWriter writer = new FileWriter("TreeSet_Timing_Results_ADD_ALL.csv")) {

            // Run the Random BST experiment
            writer.write("Problem Size (N),TreeSet ADD_ALL Time (ns)\n");
            Timer_TreeSet_Add_All TreeSetAddAll = new Timer_TreeSet_Add_All(problemSizes, timesToLoop);
            TimerTemplate.Result[] TreeSetAddAllResults = TreeSetAddAll.run();

            // Write results to CSV
            for (int i = 0; i < problemSizes.length; i++) {
                writer.write(String.format("%d,%.2f\n",
                        TreeSetAddAllResults[i].n(),
                        TreeSetAddAllResults[i].avgNanoSecs()));
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

