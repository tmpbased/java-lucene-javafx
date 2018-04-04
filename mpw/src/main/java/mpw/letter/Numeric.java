package mpw.letter;

public final class Numeric extends AnyCharOfStr {
  public static final Numeric INSTANCE = new Numeric();

  private Numeric() {}

  @Override
  protected String chars() {
    return "0123456789";
  }

  @Override
  public String toString() {
    return String.format("numeric (%s)", super.toString());
  }
}
