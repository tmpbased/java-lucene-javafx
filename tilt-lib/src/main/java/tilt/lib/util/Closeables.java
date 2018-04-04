package tilt.lib.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public final class Closeables implements Closeable {
  private List<Closeable> closeables;

  public Closeables() {
    this.closeables = new ArrayList<>();
  }

  public <T extends Closeable> T add(final T closeable) {
    this.closeables.add(closeable);
    return closeable;
  }

  @Override
  public void close() {
    final ListIterator<Closeable> it = this.closeables.listIterator(this.closeables.size());
    while (it.hasPrevious()) {
      final Closeable closeable = it.previous();
      try {
        closeable.close();
      } catch (final IOException e) {
        // TODO logging
        e.printStackTrace();
      }
      it.remove();
    }
  }
}
