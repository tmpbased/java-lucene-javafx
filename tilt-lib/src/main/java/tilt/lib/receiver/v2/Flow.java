package tilt.lib.receiver.v2;

public abstract class Flow {
  Flow() {
  }

  public abstract void send(Event event);
}
