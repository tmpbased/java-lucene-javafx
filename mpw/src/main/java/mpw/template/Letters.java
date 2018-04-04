package mpw.template;

import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.List;

import mpw.letter.Letter;

final class Letters extends AbstractList<Letter> implements Template {
  private final List<Letter> letters;

  public Letters(final List<Letter> letters) {
    this.letters = letters;
  }

  @Override
  public Letter get(final int index) {
    return this.letters.get(index);
  }

  @Override
  public int size() {
    return this.letters.size();
  }

  @Override
  public String render(final ByteBuffer buffer) {
    final StringBuilder sb = new StringBuilder();
    for (final Letter letter : this.letters) {
      sb.append(letter.render(buffer));
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return this.letters.toString();
  }
}
