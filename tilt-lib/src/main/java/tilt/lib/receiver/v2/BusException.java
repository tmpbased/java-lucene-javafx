package tilt.lib.receiver.v2;

public final class BusException extends RuntimeException implements Event {
  private static final long serialVersionUID = 6139539054864813791L;

  BusException(final String message) {
    super(message, null, false, false);
  }
  
  BusException(final String message, final Throwable cause) {
    super(message, cause, false, false);
  }
}