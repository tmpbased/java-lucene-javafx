package mpw.letter;

public final class ConsonantsLowerCase extends AnyCharOfStr {
  public static final ConsonantsLowerCase INSTANCE = new ConsonantsLowerCase();

  private ConsonantsLowerCase() {}

  @Override
  protected String chars() {
    return "bcdfghjklmnpqrstvwxyz";
  }

  @Override
  public String toString() {
    return String.format("consonants (%s)", super.toString());
  }
}
