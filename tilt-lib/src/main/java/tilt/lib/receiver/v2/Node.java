package tilt.lib.receiver.v2;

import java.lang.reflect.Modifier;
import java.util.Objects;

public abstract class Node extends Consumer {
  private static boolean isInheritableClass(final Class<?> clazz) {
    return (clazz.getModifiers() & Modifier.FINAL) == 0;
  }

  private Flow flow;

  public Node() {
  }

  protected void setUp(final Flow flow) {
    if (isInheritableClass(getClass())) {
      throw new BusException("Unexpectedly inheritable node " + getClass());
    }
    if (flow != null) {
      this.flow = flow;
    }
  }

  protected void tearDown() {
    this.flow = null;
  }

  protected final void send(final Event event) {
    Objects.requireNonNull(event, "event == null");
    if (isInheritableClass(event.getClass())) {
      throw new BusException("Unexpectedly inheritable event " + event.getClass());
    }
    final Flow flow = this.flow;
    if (flow == null) {
      throw new BusException("Bus is not set up");
    }
    flow.send(event);
  }

  @Override
  protected abstract void consume(Flow flow, Event event);

  @Override
  public final int hashCode() {
    return super.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return super.equals(obj);
  }
}
