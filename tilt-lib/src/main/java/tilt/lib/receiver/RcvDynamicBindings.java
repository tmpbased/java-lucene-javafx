package tilt.lib.receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RcvDynamicBindings implements Receiver {
  private final Map<Class<?>, Receiver> receivers;
  private Receiver defaultReceiver;

  public RcvDynamicBindings() {
    this(Receiver.none());
  }

  public RcvDynamicBindings(final Receiver defaultReceiver) {
    this.receivers = new HashMap<>();
    this.defaultReceiver = defaultReceiver;
  }

  public void setDefaultReceiver(Receiver defaultReceiver) {
    this.defaultReceiver = Objects.requireNonNull(defaultReceiver, "defaultReceiver == null");
  }

  public RcvDynamicBindings add(final RcvTypeBinding binding) {
    this.receivers.put(binding.type(), binding.receiver());
    return this;
  }

  public RcvDynamicBindings clear() {
    this.receivers.clear();
    return this;
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.receivers.getOrDefault(event.getClass(), this.defaultReceiver).onEvent(source, event);
  }
}
