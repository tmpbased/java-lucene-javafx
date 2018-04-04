package mpw.letter;

import java.util.Arrays;

public final class Alphabetic extends JoinedStr {
  public static final Alphabetic INSTANCE = new Alphabetic();

  private Alphabetic() {
    super(Arrays.asList(VowelsUpperCase.INSTANCE, VowelsLowerCase.INSTANCE,
        ConsonantsUpperCase.INSTANCE, ConsonantsLowerCase.INSTANCE));
  }

  @Override
  public String toString() {
    return String.format("alphabetic (%s)", super.toString());
  }
}
