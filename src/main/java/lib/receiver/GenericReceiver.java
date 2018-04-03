package lib.receiver;

@FunctionalInterface
public interface GenericReceiver<E> {
  void onEvent(Receiver source, E event);
}
