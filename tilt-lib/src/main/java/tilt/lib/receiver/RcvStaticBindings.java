package tilt.lib.receiver;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RcvStaticBindings {
  private final List<RcvTypeBinding> bindings;
  private final Receiver defaultReceiver;

  public RcvStaticBindings() {
    this(Receiver.none());
  }

  public RcvStaticBindings(final Receiver defaultReceiver) {
    this.bindings = new ArrayList<>();
    this.defaultReceiver = defaultReceiver;
  }

  public RcvStaticBindings add(final RcvTypeBinding binding) {
    this.bindings.add(binding);
    return this;
  }

  static final class StaticMap implements Receiver {
    private final Map<Class<?>, Receiver> receivers;
    private final Receiver defaultReceiver;

    public StaticMap(final Map<Class<?>, Receiver> receivers, final Receiver defaultReceiver) {
      this.receivers = new HashMap<>(receivers);
      this.defaultReceiver = defaultReceiver;
    }

    @Override
    public void onEvent(Receiver source, Object event) {
      this.receivers.getOrDefault(event.getClass(), this.defaultReceiver).onEvent(source, event);
    }
  }

  private void applyTo(final Map<Class<?>, Receiver> receivers) {
    receivers.putAll(this.bindings.stream().collect(groupingBy(RcvTypeBinding::type,
        mapping(RcvTypeBinding::receiver, collectingAndThen(toList(), RcvIterableGroup::new)))));
  }

  public Receiver build() {
    final Map<Class<?>, Receiver> receivers = new HashMap<>();
    applyTo(receivers);
    return new StaticMap(receivers, this.defaultReceiver);
  }
}
