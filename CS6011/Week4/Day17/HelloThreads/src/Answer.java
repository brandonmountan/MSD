public class Answer {
    static int answer;

    public static void main(String[] args) throws InterruptedException {
        badSum();
    }

    public static void badSum() throws InterruptedException {
        answer = 0;
        int maxValue = 100;
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int finalI = i;
            threads[i] = new Thread(() -> {
                for (int j = finalI * maxValue / numThreads; j < Math.min((finalI + 1) * maxValue / numThreads, maxValue); j++) {
                    answer += j;
                }
            });
        }
        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();
        System.out.println("Computed answer: " + answer);
        System.out.println("Correct answer: " + (maxValue * (maxValue - 1) / 2)); // Note: maxValue + 1 because sum includes maxValue
    }
}
