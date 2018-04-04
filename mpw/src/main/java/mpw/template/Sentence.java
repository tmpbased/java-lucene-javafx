package mpw.template;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mpw.api.Renderer;
import mpw.letter.Letter;

public final class Sentence implements Renderer<Template> {
  public static final class Builder {
    private final List<Renderer<? extends Template>> words;
    private Letter separator;

    public Builder() {
      this.words = new ArrayList<>();
      this.separator = null;
    }

    public Builder add(final Renderer<? extends Template> word) {
      this.words.add(word);
      return this;
    }

    public Builder setSeparator(final Letter separator) {
      this.separator = separator;
      return this;
    }

    public Sentence build() {
      return new Sentence(this);
    }
  }

  private final List<Renderer<? extends Template>> words;
  private final Letter separator;

  private Sentence(final Builder builder) {
    this.words = List.copyOf(builder.words);
    this.separator = builder.separator;
  }

  @Override
  public Template render(final ByteBuffer buffer) {
    final List<Letter> letters = new ArrayList<>();
    int maxPos = buffer.position();
    final var wordIt = this.words.iterator();
    while (wordIt.hasNext()) {
      final var word = wordIt.next();
      final ByteBuffer slice = buffer.slice();
      letters.addAll(word.render(slice)); // TODO share RNG
      maxPos = Math.max(maxPos, slice.position()); // TODO minimum?
      if (this.separator != null && wordIt.hasNext()) {
        letters.add(this.separator);
      }
      buffer.get(); // This changes RNG's seed for the next word.
    }
    if (maxPos > buffer.position()) {
      buffer.position(maxPos);
    }
    return new Letters(Collections.unmodifiableList(letters));
  }

  @Override
  public String toString() {
    return String.format("Sentence %s", this.words);
  }
}
