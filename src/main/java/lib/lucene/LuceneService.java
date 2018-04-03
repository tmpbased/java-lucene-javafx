package lib.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import lib.IndexRequest;
import lib.SearchRequest;
import lib.receiver.GenericReceiver;
import lib.receiver.Receiver;
import lib.receiver.ReceiverBuilder;
import lib.receiver.RunReceiver;

public final class LuceneService implements RunReceiver {
  private final Path indexPath;

  private static final class Event {
    final Receiver source;
    final Object event;

    public Event(final Receiver source, final Object event) {
      this.source = source;
      this.event = event;
    }
  }

  private final LinkedBlockingQueue<Event> queue;

  public LuceneService(final Path indexPath) {
    this.indexPath = indexPath;
    this.queue = new LinkedBlockingQueue<>();
  }

  @Override
  public void onEvent(final Receiver source, final Object event) {
    this.queue.add(new Event(source, event));
  }

  private <E> Receiver obtrudeRequestSource(Receiver receiver) {
    return (source, event) -> receiver.onEvent(this, event);
  }

  private <E> GenericReceiver<E> obtrudeResponseSource(GenericReceiver<E> receiver) {
    return (source, event) -> receiver.onEvent(obtrudeRequestSource(source), event);
  }

  @Override
  public void run() {
    try (final Directory dir = FSDirectory.open(this.indexPath);
        final SearchSession searchSession = new SearchSession(dir);
        final IndexSession indexSession = new IndexSession(dir)) {
      final Receiver receiver = new ReceiverBuilder()
          .mapCast(SearchRequest.class, obtrudeResponseSource(searchSession::search))
          .mapCast(IndexRequest.class, obtrudeResponseSource(indexSession::index))
          .build();
      while (true) {
        final Event event = this.queue.take();
        receiver.onEvent(event.source, event.event);
      }
    } catch (final IOException | InterruptedException e) {
      // TODO logging
      e.printStackTrace();
    }
  }
}
