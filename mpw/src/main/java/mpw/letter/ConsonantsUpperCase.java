package mpw.letter;

public final class ConsonantsUpperCase extends AnyCharOfStr {
  public static final ConsonantsUpperCase INSTANCE = new ConsonantsUpperCase();

  private ConsonantsUpperCase() {}

  @Override
  protected String chars() {
    return "BCDFGHJKLMNPQRSTVWXYZ";
  }

  @Override
  public String toString() {
    return String.format("consonants (%s)", super.toString());
  }
}
