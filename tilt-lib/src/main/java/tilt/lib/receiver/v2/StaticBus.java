package tilt.lib.receiver.v2;

import java.util.Map;

public final class StaticBus extends Bus {
  private final Map<Node, Consumer> consumers;

  public StaticBus(final Map<Node, Consumer> consumers) {
    this.consumers = Map.copyOf(consumers);
  }

  private final class BsFlow extends Flow {
    private final Consumer consumer;

    public BsFlow(final Consumer consumer) {
      this.consumer = consumer;
    }

    @Override
    public void send(final Event event) {
      this.consumer.consume(FwError.INSTANCE, event);
    }
  }

  @Override
  public void setUp() {
    this.consumers.forEach((k, v) -> {
      k.setUp(new BsFlow(this.consumers.getOrDefault(k, CrFlow.INSTANCE)));
      v.setUp(null);
    });
  }

  @Override
  public void tearDown() {
    this.consumers.forEach((k, v) -> {
      k.tearDown();
      v.tearDown();
    });
  }
}