public class MultithreadingLab {
    public static void main(String[] args) {
        // Uncomment one of the lines below to test either sayHello or badSum
        // sayHello();
        badSum();
    }

    public static void badSum() {
        SumTask.resetAnswer(); // Reset the shared answer
        int numThreads = 1; // Adjust as needed
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new SumTask(i));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int correctAnswer = (SumTask.MAX_VALUE * (SumTask.MAX_VALUE - 1)) / 2;
        System.out.println("Computed answer: " + SumTask.getAnswer());
        System.out.println("Correct answer: " + correctAnswer);
    }
}
