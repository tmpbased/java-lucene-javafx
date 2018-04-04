package tilt.lib.receiver;

import java.util.Arrays;
import java.util.function.Consumer;

@FunctionalInterface
public interface RcvGeneric<E> {
  void onEvent(Receiver source, E event);

  static <E> RcvGeneric<E> consumeEvent(final Consumer<E> eventConsumer) {
    return (source, event) -> eventConsumer.accept(event);
  }

  @SafeVarargs
  static <E> RcvGeneric<E> sequence(RcvGeneric<E>... receivers) {
    return (source, event) -> Arrays.stream(receivers).forEach(receiver -> receiver.onEvent(source, event));
  }
}
