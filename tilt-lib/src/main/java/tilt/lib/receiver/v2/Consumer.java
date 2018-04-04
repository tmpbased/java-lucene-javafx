package tilt.lib.receiver.v2;

public abstract class Consumer {
  Consumer() {
  }

  abstract protected void consume(Flow flow, Event event);

  protected void setUp(Flow flow) {
  }

  protected void tearDown() {
  }
}
