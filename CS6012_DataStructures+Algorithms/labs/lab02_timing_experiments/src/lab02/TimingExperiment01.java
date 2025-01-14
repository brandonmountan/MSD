package lab02;

public class TimingExperiment01 {

  public static void main(String[] args) {
    long lastTime = System.currentTimeMillis();
    int advanceCount = 0;
    int continueCount = 0;
    while (advanceCount < 100) {
      long currentTime = System.currentTimeMillis();
      if (currentTime == lastTime) {
        continueCount++;
        continue;
      }
      System.out.println("Time advanced " + (currentTime - lastTime) + " milliseconds.");
      lastTime = currentTime;
      advanceCount++;
    }
    System.out.println("total continues executed: " + continueCount);
    System.out.println("advance count executed: " + advanceCount);
  }
}
