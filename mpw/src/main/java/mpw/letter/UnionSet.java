package mpw.letter;

public final class UnionSet extends AnyCharOfStr {
  public static final UnionSet INSTANCE = new UnionSet();

  private UnionSet() {}

  @Override
  protected String chars() {
    return "AEIOUaeiouBCDFGHJKLMNPQRSTVWXYZbcdfghjklmnpqrstvwxyz0123456789!@#$%^&*()";
  }

  @Override
  public String toString() {
    return String.format("union set (%s)", super.toString());
  }
}
