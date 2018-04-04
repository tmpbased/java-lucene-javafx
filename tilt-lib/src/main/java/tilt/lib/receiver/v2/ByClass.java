package tilt.lib.receiver.v2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ByClass extends Consumer {
  private final ConcurrentMap<Class<?>, Consumer> consumers;

  public ByClass(final Map<Class<?>, Consumer> consumers) {
    this.consumers = new ConcurrentHashMap<>(consumers);
  }

  @Override
  protected void consume(final Flow flow, final Event event) {
    final Consumer consumer = this.consumers.getOrDefault(event.getClass(), CrFlow.INSTANCE);
    consumer.consume(flow, event);
  }
  
  @Override
  protected void setUp(Flow flow) {
    this.consumers.values().forEach(it -> it.setUp(flow));
  }
  
  @Override
  protected void tearDown() {
    this.consumers.values().forEach(Consumer::tearDown);
  }
}