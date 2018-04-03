package lib.receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ReceiverBuilder {
  private static final class Any<E> implements Receiver {
    private final Class<E> type;
    private final GenericReceiver<E> receiver;

    Any(final Class<E> type, final GenericReceiver<E> receiver) {
      this.type = type;
      this.receiver = receiver;
    }

    @Override
    public void onEvent(final Receiver source, final Object event) {
      this.receiver.onEvent(source, this.type.cast(event));
    }
  }

  private final Map<Class<?>, Receiver> receivers;

  public ReceiverBuilder() {
    this.receivers = new HashMap<>();
  }

  public <E> ReceiverBuilder mapCast(final Class<E> type, final GenericReceiver<E> receiver) {
    this.receivers.put(type, new Any<>(type, receiver));
    return this;
  }

  public ReceiverBuilder map(final Class<?> type, final Receiver receiver) {
    this.receivers.put(type, receiver);
    return this;
  }

  private static final class Mapped implements Receiver {
    private final ConcurrentMap<Class<?>, Receiver> receivers;

    Mapped(final Map<Class<?>, Receiver> receivers) {
      this.receivers = new ConcurrentHashMap<>(receivers);
    }

    @Override
    public void onEvent(final Receiver source, final Object event) {
      final Receiver receiver = this.receivers.get(event.getClass());
      if (receiver != null) {
        receiver.onEvent(source, event);
      }
    }
  }

  public Receiver build() {
    return new Mapped(this.receivers);
  }
}
