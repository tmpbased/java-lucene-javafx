package mpw.letter;

import java.util.Arrays;

public final class AlphabeticUpperCase extends JoinedStr {
  public static final AlphabeticUpperCase INSTANCE = new AlphabeticUpperCase();

  private AlphabeticUpperCase() {
    super(Arrays.asList(VowelsUpperCase.INSTANCE, ConsonantsUpperCase.INSTANCE));
  }

  @Override
  public String toString() {
    return String.format("alphabetic (%s)", super.toString());
  }
}
