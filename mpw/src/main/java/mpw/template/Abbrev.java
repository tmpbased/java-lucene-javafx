package mpw.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import mpw.letter.Alphabetic;
import mpw.letter.AlphabeticUpperCase;
import mpw.letter.ConsonantsLowerCase;
import mpw.letter.ConsonantsUpperCase;
import mpw.letter.Letter;
import mpw.letter.Numeric;
import mpw.letter.Other;
import mpw.letter.UnionSet;
import mpw.letter.VowelsLowerCase;
import mpw.letter.VowelsUpperCase;

public final class Abbrev {
  public static final class Builder {
    private final Map<Character, Letter> map;
    private final Map<Letter, Character> reverseMap;

    public Builder() {
      this.map = new HashMap<>();
      this.reverseMap = new HashMap<>();
    }

    public Builder add(final char ch, final Letter charClass) {
      this.map.put(ch, charClass);
      this.reverseMap.put(charClass, ch);
      return this;
    }

    public Abbrev build() {
      return new Abbrev(this);
    }
  }

  public static final Abbrev CANONICAL =
      new Abbrev.Builder().add('V', VowelsUpperCase.INSTANCE).add('C', ConsonantsUpperCase.INSTANCE)
          .add('v', VowelsLowerCase.INSTANCE).add('c', ConsonantsLowerCase.INSTANCE)
          .add('A', AlphabeticUpperCase.INSTANCE).add('a', Alphabetic.INSTANCE)
          .add('n', Numeric.INSTANCE).add('o', Other.INSTANCE).add('x', UnionSet.INSTANCE).build();

  private final Map<Character, Letter> map;
  private final Map<Letter, Character> reverseMap;

  private Abbrev(final Builder builder) {
    this.map = Map.copyOf(builder.map);
    this.reverseMap = Map.copyOf(builder.reverseMap);
  }

  public Letter expand(char ch) {
    return Objects.requireNonNull(this.map.get(ch),
        () -> String.format("No expansion for the abbreviation '%s'", ch));
  }

  public char abbreviate(Letter letter) {
    return Objects.requireNonNull(this.reverseMap.get(letter),
        () -> String.format("No abbreviation for '%s'", letter));
  }
}
