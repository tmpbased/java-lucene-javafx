package tilt.lib.receiver.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Unmodifiable non-empty list of non-null elements, representing a sequence of
 * nodes to receive an event.
 */
public final class CrSeq extends Consumer {
  private final class CrNext extends Consumer {
    private final int index;

    private CrNext(final int index) {
      this.index = index;
    }

    @Override
    public void consume(final Flow flow, final Event event) {
      final Consumer consumer = get(this.index + 1);
      if (consumer == null) {
        flow.send(event);
      } else {
        consumer.consume(flow, event);
      }
    }
  }

  private final List<Consumer> consumers;

  public CrSeq(final Collection<? extends Consumer> consumers) {
    this.consumers = new ArrayList<>();
    if (consumers.isEmpty()) {
      throw new IllegalStateException("no consumers");
    }
    for (final Consumer consumer : consumers) {
      add(consumer);
    }
  }

  private void add(Consumer consumer) {
    final CrNext ctx = new CrNext(this.consumers.size());
    this.consumers.add(new CrJoin(consumer, ctx));
  }

  private Consumer get(int index) {
    return Optional.ofNullable(this.consumers.get(index)).orElse(null);
  }

  @Override
  protected void consume(final Flow flow, final Event event) {
    this.consumers.get(0).consume(flow, event);
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
    return "Seq" + this.consumers;
  }
}
