package tilt.lib.receiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public final class RcvBlockingQueue implements RcvAsync {
  public static final class Close {
  }

  public static final class Subscribe {
  }

  public static final class RemoveSubscription {
  }

  public static final class Event {
    final Receiver source;
    final Object event;

    Event(final Receiver source, final Object event) {
      this.source = source;
      this.event = event;
    }
  }

  private final Collection<Receiver> receivers;
  private final BlockingQueue<Event> queue;

  public RcvBlockingQueue(final BlockingQueue<Event> queue) {
    this.receivers = new ArrayList<>();
    this.queue = queue;
  }

  @Override
  public void onEvent(final Receiver source, final Object event) {
    this.queue.add(new Event(source, event));
  }

  @Override
  public void run() {
    while (true) {
      try {
        final Event event = this.queue.take();
        if (event.event instanceof Close) {
          break;
        }
        if (event.event instanceof Subscribe) {
          this.receivers.add(event.source);
          continue;
        }
        if (event.event instanceof RemoveSubscription) {
          this.receivers.remove(event.source);
          continue;
        }
        for (final Receiver receiver : this.receivers) {
          receiver.onEvent(event.source, event.event);
        }
      } catch (final InterruptedException e) {
        // TODO logging
        e.printStackTrace();
      }
    }
  }
}
