import java.util.HashMap;

public class Router {

    private HashMap<Router, Integer> distances;
    private String name;
    public Router(String name) {
        this.distances = new HashMap<>();
        this.name = name;
    }

    /**
     * Initializes the router's distance table and broadcasts its initial distances to all neighbors.
     * This method is called when the network starts up.
     *
     * @throws InterruptedException if the thread is interrupted while sending messages.
     */
    public void onInit() throws InterruptedException {
        // Initialize the distance to self as 0 (this router is 0 hops away from itself).
        distances.put(this, 0);

        // Add all directly connected neighbors to the distance table.
        // Iterate through each neighbor of this router.
        for (Neighbor neighbor : Network.getNeighbors(this)) {
            // Add the neighbor to the distance table with the cost of the direct link.
            distances.put(neighbor.router, neighbor.cost);
        }

        // Broadcast the initial distance table to all neighbors.
        // Iterate through each neighbor of this router.
        for (Neighbor neighbor : Network.getNeighbors(this)) {
            // Create a copy of the current distance table to send to the neighbor.
            HashMap<Router, Integer> copy = new HashMap<>(distances);

            // Create a message containing the distance table and send it to the neighbor.
            Message msg = new Message(this, neighbor.router, copy);
            Network.sendDistanceMessage(msg);
        }
    }

    /**
     * Processes a distance message received from a neighbor and updates the local distance table.
     * If the distance table is updated, the new distances are broadcast to all neighbors.
     *
     * @param message The distance message received from a neighbor.
     * @throws InterruptedException if the thread is interrupted while sending messages.
     */
    public void onDistanceMessage(Message message) throws InterruptedException {
        // Flag to track whether the distance table was updated.
        boolean updated = false;

        // Extract the sender of the message.
        Router sender = message.sender;

        // Get the cost to reach the sender from this router's distance table.
        Integer costToSender = distances.get(sender);

        // If the sender is not in the distance table, exit (should not happen in a valid network).
        if (costToSender == null) return;

        // Iterate through all entries in the received distance vector.
        for (HashMap.Entry<Router, Integer> entry : message.distances.entrySet()) {
            // Extract the destination router and the sender's advertised distance to it.
            Router dest = entry.getKey();
            int senderDist = entry.getValue();

            // Calculate the total cost to reach the destination via the sender.
            int totalCost = costToSender + senderDist;

            // Get the current known distance to the destination (default to infinity if unknown).
            int currentDist = distances.getOrDefault(dest, Integer.MAX_VALUE);

            // If the new total cost is lower than the current known distance, update the distance table.
            if (totalCost < currentDist) {
                distances.put(dest, totalCost);
                updated = true; // Mark that the distance table was updated.
            }
        }

        // If the distance table was updated, broadcast the new distances to all neighbors.
        if (updated) {
            // Iterate through each neighbor of this router.
            for (Neighbor neighbor : Network.getNeighbors(this)) {
                // Create a copy of the updated distance table to send to the neighbor.
                HashMap<Router, Integer> copy = new HashMap<>(distances);

                // Create a message containing the updated distance table and send it to the neighbor.
                Message msg = new Message(this, neighbor.router, copy);
                Network.sendDistanceMessage(msg);
            }
        }
    }


    public void dumpDistanceTable() {
        System.out.println("router: " + this);
        for(Router r : distances.keySet()){
            System.out.println("\t" + r + "\t" + distances.get(r));
        }
    }

    @Override
    public String toString(){
        return "Router: " + name;
    }
}
