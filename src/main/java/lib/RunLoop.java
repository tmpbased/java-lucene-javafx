package lib;

public final class RunLoop implements Runnable {
  private final Runnable runnable;

  public RunLoop(final Runnable runnable) {
    this.runnable = runnable;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      this.runnable.run();
    }
  }

  public static void main(String[] args) throws Exception {
    final RunQueue r = new RunQueue((source, event) -> System.out.println(event));
    final Thread t = new Thread(new RunLoop(r));
    t.start();
    r.onEvent(null, "abc");
    Thread.sleep(1000);
    t.interrupt();
  }
}
