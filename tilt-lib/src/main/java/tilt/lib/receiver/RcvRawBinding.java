package tilt.lib.receiver;

public final class RcvRawBinding implements RcvTypeBinding {
  private final Class<?> type;
  private final Receiver receiver;

  public RcvRawBinding(final Class<?> type, final Receiver receiver) {
    this.type = type;
    this.receiver = receiver;
  }

  @Override
  public Class<?> type() {
    return this.type;
  }

  @Override
  public Receiver receiver() {
    return this.receiver;
  }
}