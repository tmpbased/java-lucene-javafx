package tilt.lib.receiver;

public final class RcvIterableGroup implements Receiver {
  private final Iterable<? extends Receiver> receivers;

  public RcvIterableGroup(final Iterable<? extends Receiver> receivers) {
    this.receivers = receivers;
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    for (final Receiver receiver : this.receivers) {
      receiver.onEvent(source, event);
    }
  }
}
