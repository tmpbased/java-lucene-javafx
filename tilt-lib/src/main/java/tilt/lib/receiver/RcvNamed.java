package tilt.lib.receiver;

public final class RcvNamed implements Receiver {
  private String name;
  private Receiver receiver;

  public RcvNamed(final String name, final Receiver receiver) {
    this.name = name;
    this.receiver = receiver;
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.receiver.onEvent(source, event);
  }

  @Override
  public String toString() {
    return String.format("%s", this.name);
  }
}
