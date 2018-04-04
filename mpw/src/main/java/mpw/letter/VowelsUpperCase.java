package mpw.letter;

public final class VowelsUpperCase extends AnyCharOfStr {
  public static final VowelsUpperCase INSTANCE = new VowelsUpperCase();

  private VowelsUpperCase() {}

  @Override
  protected String chars() {
    return "AEIOU";
  }

  @Override
  public String toString() {
    return String.format("vowels (%s)", super.toString());
  }
}
