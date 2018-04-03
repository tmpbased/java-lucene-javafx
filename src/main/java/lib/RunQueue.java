package lib;

import java.util.concurrent.LinkedBlockingQueue;

import lib.receiver.Receiver;
import lib.receiver.RunReceiver;

public final class RunQueue implements RunReceiver {
  private static final class Event {
    final Receiver source;
    final Object event;

    public Event(final Receiver source, final Object event) {
      this.source = source;
      this.event = event;
    }
  }

  private final Receiver receiver;
  private final LinkedBlockingQueue<Event> queue;

  public RunQueue(final Receiver receiver) {
    this.receiver = receiver;
    this.queue = new LinkedBlockingQueue<>();
  }

  @Override
  public void onEvent(final Receiver source, final Object event) {
    this.queue.add(new Event(source, event));
  }

  @Override
  public void run() {
      try {
        final Event event = this.queue.take();
        this.receiver.onEvent(event.source, event.event);
      } catch (final InterruptedException e) {
        // TODO log?
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
  }
}
