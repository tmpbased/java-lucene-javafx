package mpw.template;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import mpw.api.Renderer;
import mpw.lang.Mappers;
import mpw.letter.Letter;
import mpw.template.limit.Limit;
import mpw.template.limit.Limits;
import mpw.template.weight.Weight;

public final class Word implements Renderer<Template> {
  public static final Weight DEFAULT_WEIGHT = Weight.Fixed.ONE;
  private static final int MIN_LENGTH = -1;

  private static <T> Limits limits(Map<T, Limit> map) {
    return map.entrySet().stream().map(Mappers.destructMapEntry(Limited<T>::new))
        .collect(Collectors.collectingAndThen(Collectors.toList(), Limits::new));
  }

  /**
   * TODO need rules to prevent / reject some bad words.
   *
   * E.g. limit consecutive uses of the same Letter: 3v3c => vvvccc ccvvvc => BAD, vvcvcc cvcvcv => OK
   */
  public static final class Builder {
    private final Map<Letter, Limit> limits;
    private int length;

    public Builder() {
      this.limits = new LinkedHashMap<>();
      this.length = MIN_LENGTH;
    }

    public Builder setCharLimit(final Letter letter, final Limit limit) { // TODO + weight
      this.limits.put(letter, limit);
      return this;
    }

    public Builder setLength(final int length) {
      if (length <= 0) {
        throw new IllegalArgumentException(
            String.format("Word.setLength(%s): length <= 0", length));
      }
      this.length = length;
      return this;
    }

    public Word build() {
      if (this.length != MIN_LENGTH) {
        final Limits limits = limits(this.limits);
        final int minLength = limits.getMinLength();
        if (this.length < minLength) {
          throw new IllegalStateException(
              String.format("Word [%s] %s: length < %s", this.length, limits, minLength));
        }
        final int maxLength = limits.getMaxLength();
        if (this.length > maxLength) {
          throw new IllegalStateException(
              String.format("Word [%s] %s: length > %s", this.length, limits, maxLength));
        }
      }
      return new Word(this);
    }
  }

  private static final class UsedLimit {
    final Letter letter;
    final Limit limit;
    int used;

    public UsedLimit(final Letter letter, final Limit limit) {
      this.letter = letter;
      this.limit = limit;
      this.used = limit.getMinCount();
    }

    public boolean isMaxReached() {
      return this.limit.matches(this.used + 1) == false;
    }

    public void use() {
      this.used++;
    }
  }

  private final Map<Letter, Limit> limits;
  private final int length;

  private Word(final Builder builder) {
    this.limits = new LinkedHashMap<>(builder.limits);
    this.length = builder.length;
  }

  private List<UsedLimit> getUsedLimits() {
    return this.limits.entrySet().stream().map(Mappers.destructMapEntry(UsedLimit::new))
        .filter(it -> it.isMaxReached() == false).collect(Collectors.toList());
  }

  @Override
  public Template render(final ByteBuffer buffer) {
    final long seed = buffer.order(ByteOrder.BIG_ENDIAN).getLong();
    final Random rnd = new Random(seed);
    final List<Letter> letters = this.limits.entrySet().stream()
        .flatMap(it -> Collections.nCopies(it.getValue().getMinCount(), it.getKey()).stream())
        .collect(Collectors.toList());
    if (this.length != MIN_LENGTH && letters.size() < this.length) {
      final List<UsedLimit> usedLimits = getUsedLimits();
      while (letters.size() < this.length && usedLimits.isEmpty() == false) {
        final int i = rnd.nextInt(usedLimits.size()); // TODO weighted?
        final UsedLimit usedLimit = usedLimits.get(i);
        letters.add(usedLimit.letter);
        usedLimit.use();
        if (usedLimit.isMaxReached()) {
          usedLimits.remove(i); // TODO ArrayList.remove(int)
        }
      }
    }
    Collections.shuffle(letters, rnd);
    return new Letters(Collections.unmodifiableList(letters));
  }

  @Override
  public String toString() {
    final Limits limits = limits(this.limits);
    final int length = this.length == MIN_LENGTH ? limits.getMinLength() : this.length;
    return String.format("Word [%s] %s", length, limits);
  }
}
