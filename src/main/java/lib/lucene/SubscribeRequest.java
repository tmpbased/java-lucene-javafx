package lib.lucene;

import lib.receiver.Receiver;

final class SubscribeRequest {
  public final Class<?> eventType;
  public final Receiver receiver;

  public SubscribeRequest(final Class<?> eventType, final Receiver receiver) {
    this.eventType = eventType;
    this.receiver = receiver;
  }
}
