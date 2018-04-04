package tilt.lib.receiver.v2;

final class CrFlow extends Consumer {
  static final CrFlow INSTANCE = new CrFlow();

  private CrFlow() {
  }

  @Override
  public void consume(Flow flow, Event event) {
    flow.send(event);
  }
}
