package lib.receiver;

public interface Receiver extends GenericReceiver<Object> {
  static Receiver none() {
    return (source, event) -> {};
  }
}
