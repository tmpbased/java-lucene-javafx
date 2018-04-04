package mpw.api;

import java.nio.ByteBuffer;

public interface Renderer<T> {
  T render(ByteBuffer buffer);
}
