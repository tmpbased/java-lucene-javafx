package tilt.lib.receiver.v2;

final class FwConsumer extends Flow {
  private final Consumer seq;
  private final Flow flow;

  public FwConsumer(final Consumer seq, final Flow flow) {
    this.seq = seq;
    this.flow = flow;
  }

  @Override
  public void send(Event event) {
    this.seq.consume(this.flow, event);
  }
}