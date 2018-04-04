package tilt.lib.receiver.v2;

final class CrJoin extends Consumer {
  private final Consumer a, b;

  CrJoin(final Consumer a, final Consumer b) {
    this.a = a;
    this.b = b;
  }

  @Override
  protected void consume(Flow flow, Event event) {
    this.a.consume(new FwConsumer(this.b, flow), event);
  }
  
  @Override
  protected void setUp(Flow flow) {
    this.a.setUp(flow);
    this.b.setUp(flow);
  }
  
  @Override
  protected void tearDown() {
    this.a.tearDown();
    this.b.tearDown();
  }
}