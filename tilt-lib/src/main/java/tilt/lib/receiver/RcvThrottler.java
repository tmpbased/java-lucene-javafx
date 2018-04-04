package tilt.lib.receiver;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RcvThrottler implements Runnable {
  private final class Throttled implements Receiver {
    private final Receiver receiver;
    private final ConcurrentMap<Receiver, Object> events;
    private final Duration duration;
    private Instant lastInstant;

    public Throttled(final Receiver receiver, final Duration duration) {
      this.receiver = receiver;
      this.duration = duration;
      this.events = new ConcurrentHashMap<>();
    }

    @Override
    public void onEvent(Receiver source, Object event) {
      this.events.put(source, event);
    }

    public void send() {
      final Instant currentInstant = clock.instant();
      if (this.lastInstant == null || Duration.between(this.lastInstant, currentInstant).compareTo(this.duration) > 0) {
        final Iterator<Map.Entry<Receiver, Object>> it = this.events.entrySet().iterator();
        while (it.hasNext()) {
          final Map.Entry<Receiver, Object> entry = it.next();
          this.receiver.onEvent(entry.getKey(), entry.getValue());
          it.remove();
        }
        this.lastInstant = currentInstant;
      }
    }
  }

  private final Clock clock;
  private final Set<Throttled> receivers;

  public RcvThrottler(final Clock clock) {
    this.clock = clock;
    this.receivers = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  @Override
  public void run() {
    try {
      for (final Throttled receiver : this.receivers) {
        receiver.send();
      }
    } catch (final Exception e) {
      // TODO logging
      e.printStackTrace();
    }
  }

  public Receiver throttle(final Receiver receiver, final Duration duration) {
    final Throttled throttled = new Throttled(receiver, duration);
    this.receivers.add(throttled);
    return throttled;
  }
}
