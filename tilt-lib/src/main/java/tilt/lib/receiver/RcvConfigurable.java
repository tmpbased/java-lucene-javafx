package tilt.lib.receiver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RcvConfigurable implements Receiver {
  private static final Collection<Class<?>> INTERNAL_EVENT_TYPES = Collections
      .unmodifiableList(Arrays.asList(BindType.class, Freeze.class));

  private static void checkNonInternalType(final Class<?> type) {
    if (INTERNAL_EVENT_TYPES.contains(type)) {
      throw new IllegalArgumentException("Unsupported type of binding: " + type);
    }
  }

  public static final class BindType {
    private final RcvTypeBinding binding;

    public <E> BindType(final RcvTypeBinding binding) {
      checkNonInternalType(binding.type());
      this.binding = binding;
    }

    private void bindTo(final Map<Class<?>, Receiver> receivers) {
      RcvConfigurable.bind(receivers, this.binding);
    }
  }

  public static final class Freeze {
  }

  public static final class Api {
    private Receiver source, receiver;

    public Api(final Receiver source, final Receiver receiver) {
      this.source = source;
      this.receiver = receiver;
    }

    public Api wrap(final Receiver source, final Receiver receiver) {
      this.source = source;
      this.receiver = receiver;
      return this;
    }

    public Api bindType(final RcvTypeBinding binding) {
      this.receiver.onEvent(this.source, new BindType(binding));
      return this;
    }

    public Api freeze() {
      this.receiver.onEvent(this.source, new Freeze());
      return this;
    }
  }

  private final Map<Class<?>, Receiver> receivers;

  private static void bind(final Map<Class<?>, Receiver> receivers, final RcvTypeBinding binding) {
    receivers.put(binding.type(), binding.receiver());
  }

  private static void freeze(final Map<Class<?>, Receiver> receivers) {
    receivers.keySet().removeAll(INTERNAL_EVENT_TYPES);
  }

  public RcvConfigurable() {
    this.receivers = new HashMap<>();
    bind(this.receivers,
        new RcvGenericBinding(BindType.class, RcvGeneric.consumeEvent(event -> event.bindTo(this.receivers))));
    bind(this.receivers, new RcvRawBinding(Freeze.class, Receiver.running(() -> freeze(this.receivers))));
  }

  @Override
  public void onEvent(final Receiver source, final Object event) {
    this.receivers.getOrDefault(event.getClass(), Receiver.none()).onEvent(source, event);
  }
}
