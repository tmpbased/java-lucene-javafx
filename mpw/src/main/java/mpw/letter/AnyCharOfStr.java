package mpw.letter;

import java.nio.ByteBuffer;

abstract class AnyCharOfStr implements Letter {
  protected abstract String chars();

  @Override
  public Character render(final ByteBuffer buffer) {
    final String chars = chars();
    final byte seed = buffer.get();
    return chars.charAt(Byte.toUnsignedInt(seed) % chars.length());
  }

  @Override
  public String toString() {
    return String.format("anyOf \"%s\"", chars());
  }
}
