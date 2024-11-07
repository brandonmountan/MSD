package lab02;

import java.util.ArrayList;

public class RemoveFrontTimer extends TimerTemplate {

    private ArrayList<Integer> list;

    /**
     * Create a timer
     *
     * @param problemSizes array of N's to use
     * @param timesToLoop  number of times to repeat the tests
     */
    public RemoveFrontTimer(int[] problemSizes, int timesToLoop) {
        super(problemSizes, timesToLoop);
        this.list = new ArrayList<>();
    }

    /**
     * Setup method: This will fill the ArrayList with 'n' elements.
     * The elements themselves don't matter, just need to create a list of size 'n'.
     */
    @Override
    protected void setup(int n) {
        list.clear();  // Clear the list before adding new elements
        for (int i = 0; i < n; i++) {
            list.add(i);  // Adding n elements, the values don't matter
        }
    }

    /**
     * Timing iteration: In this iteration, we remove the first element (index 0) from the ArrayList.
     * After removing, we add a new element to the end to maintain the size of the list.
     */
    @Override
        protected void timingIteration(int n) {
        list.remove(0);  // Remove the first element
        list.add(n);  // Add a new element to the end to maintain the list size
    }

    /**
     * Compensation iteration: In this iteration, we perform a 'set' operation at the last index of the ArrayList.
     * This is used to compensate for the time spent on the 'add' operation and should roughly match its time.
     */
    @Override
    protected void compensationIteration(int n) {
        list.set(list.size() - 1, n);  // Set the last element to 'n', compensating for the 'add' operation
    }

    public static void main(String[] args){
        // Initialize a list of problem sizes using a geometric progression which means
        // they grow by factor of 1.5 starting at 100 and going up to just under a million
        // value of n is multiplied by 1.5 at each step
        ArrayList<Integer> ns = new ArrayList<>();
        for(double n = 100; n < 1000000; n *= 1.5){
            ns.add((int)n);
        }

        //convert to primitive int[] array for problem sizes which is needed for RemoveFrontTimer constructor
        int[] problemSizes = new int[ns.size()];
        for(int i = 0; i < problemSizes.length; i++){
            problemSizes[i] = ns.get(i);
        }

        // create a timer object with the problem sizes and 10 repititions per problem size
        var timer = new RemoveFrontTimer(problemSizes, 10);
        var results = timer.run();

        // printing the results in CSV format
        System.out.println("n, time");
        for(var result: results){
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}
