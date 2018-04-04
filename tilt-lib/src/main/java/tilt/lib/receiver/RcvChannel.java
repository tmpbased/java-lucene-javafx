package tilt.lib.receiver;

public class RcvChannel implements Receiver {
  private final Receiver from, to;

  public RcvChannel(final Receiver from, final Receiver to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.to.onEvent(this.from, event);
  }
}
