package tilt.lib.receiver;

public interface Receiver extends RcvGeneric<Object> {
  static Receiver none() {
    return new RcvNamed("None", (source, event) -> {
    });
  }

  static Receiver running(Runnable action) {
    return (source, event) -> action.run();
  }
}
