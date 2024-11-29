public class SumTask implements Runnable {
    private final int threadIndex;
    protected static int MAX_VALUE = 40000;
    protected static int NUM_THREADS = 1;
    private static int answer = 0;

    public SumTask(int threadIndex) {
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        int start = threadIndex * MAX_VALUE / NUM_THREADS;
        int end = Math.min((threadIndex + 1) * MAX_VALUE / NUM_THREADS, MAX_VALUE);
        for (int j = start; j < end; j++) {
            answer += j;
        }
    }

    public static int getAnswer() {
        return answer;
    }

    public static void resetAnswer() {
        answer = 0;
    }
}
