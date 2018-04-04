package tilt.lib.receiver;

import java.util.logging.Logger;

public final class RcvIncomingLogging implements Receiver {
  private final Receiver receiver;
  private final Logger logger;

  public RcvIncomingLogging(final Receiver receiver, final Logger logger) {
    this.receiver = receiver;
    this.logger = logger;
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.logger.info(() -> String.format("%s -> %s: %s", source, this.receiver, event));
    this.receiver.onEvent(source, event);
  }
}
