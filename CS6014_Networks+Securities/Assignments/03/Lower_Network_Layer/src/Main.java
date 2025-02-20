public class Main {

    public static void main(String[] args) throws InterruptedException {
        int problemSize = 10;
        int totalMessages = 0;
        int avgMessages = 0;
        for (int i = 0; i < problemSize; i++) {
            Network.makeSimpleNetwork(); //use this for testing/debugging
            Network.makeProbablisticNetwork(256); //use this for the plotting part
            Network.dump();

            Network.startup();
            Network.runBellmanFord();

//            System.out.println("done building tables!");
            for (Router r : Network.getRouters()) {
                r.dumpDistanceTable();
            }
//            System.out.println("total messages: " + Network.getMessageCount());
            totalMessages += Network.getMessageCount();
            avgMessages = totalMessages / 10;
        }
        System.out.println("average messages: " + avgMessages);
    }
}
