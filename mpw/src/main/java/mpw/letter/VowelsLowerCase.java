package mpw.letter;

public final class VowelsLowerCase extends AnyCharOfStr {
  public static final VowelsLowerCase INSTANCE = new VowelsLowerCase();

  private VowelsLowerCase() {}

  @Override
  protected String chars() {
    return "aeiou";
  }

  @Override
  public String toString() {
    return String.format("vowels (%s)", super.toString());
  }
}
