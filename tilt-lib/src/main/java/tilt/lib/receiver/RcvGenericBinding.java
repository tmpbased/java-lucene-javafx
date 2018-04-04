package tilt.lib.receiver;

public final class RcvGenericBinding implements RcvTypeBinding {
  private final Class<?> type;
  private final Receiver receiver;

  public <E> RcvGenericBinding(final Class<E> type, final RcvGeneric<E> receiver) {
    this.type = type;
    this.receiver = new RcvCasting<>(type, receiver);
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