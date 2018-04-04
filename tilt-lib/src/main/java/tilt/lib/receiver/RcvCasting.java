package tilt.lib.receiver;

final class RcvCasting<E> implements Receiver {
  private final Class<E> type;
  private final RcvGeneric<E> receiver;

  RcvCasting(final Class<E> type, final RcvGeneric<E> receiver) {
    this.type = type;
    this.receiver = receiver;
  }

  @Override
  public void onEvent(final Receiver source, final Object event) {
    this.receiver.onEvent(source, this.type.cast(event));
  }
}