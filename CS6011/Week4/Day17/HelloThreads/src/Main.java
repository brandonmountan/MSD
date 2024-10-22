public class Main {
    // in java an interrupted exception is a checked exception that occurs when a thread is interrupted while it's waiting,
    // sleeping, or otherwise blocked
    // method .join() can throw this exception
    // it's so threads can gracefully respond to interruptions
    public static void main(String[] args) throws InterruptedException {
        sayHello();
    }

    public static void sayHello() throws InterruptedException {
        // array of 10 threads
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    // intellij says getId() is outdated and to use threadId()
                    System.out.print("hello number " + j + " from thread number " + Thread.currentThread().threadId() + " ");
                    if (j % 10 == 9) {
                        System.out.print("\n");
                    }
                }
            });
        }
        // start all threads
        for (Thread thread : threads) thread.start();
        // start all threads
        for (Thread thread : threads) thread.join();
    }
}
