package tilt.lib.receiver.v2;

import java.util.Collection;
import java.util.List;

public final class CrPar extends Consumer {
  private final List<Consumer> consumers;

  public CrPar(final Collection<? extends Consumer> consumers) {
    if (consumers.isEmpty()) {
      throw new IllegalStateException("no consumers");
    }
    this.consumers = List.copyOf(consumers);
  }

  @Override
  protected void consume(final Flow flow, final Event event) {
    for (final Consumer consumer : this.consumers) {
      consumer.consume(flow, event);
    }
  }
  
  @Override
  protected void setUp(Flow flow) {
    this.consumers.forEach(it -> it.setUp(flow));
  }
  
  @Override
  protected void tearDown() {
    this.consumers.forEach(Consumer::tearDown);
  }

  @Override
  public String toString() {
    return "Par" + this.consumers;
  }
}
