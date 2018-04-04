package mpw.letter;

import java.nio.ByteBuffer;

public class FixedChar implements Letter {
  private final char ch;

  public FixedChar(final char ch) {
    this.ch = ch;
  }

  @Override
  public Character render(ByteBuffer buffer) {
    return this.ch;
  }

  @Override
  public String toString() {
    return String.format("char (%s)", this.ch);
  }
}
