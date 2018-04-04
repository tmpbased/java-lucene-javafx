package tilt.lib.receiver.v2;

final class FwError extends Flow {
  static final FwError INSTANCE = new FwError();

  private FwError() {
  }

  @Override
  public void send(Event event) {
    throw new BusException("No handlers for event " + event.getClass());
  }
}
