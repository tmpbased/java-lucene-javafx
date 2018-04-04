package mpw.letter;

public final class Other extends AnyCharOfStr {
  public static final Other INSTANCE = new Other();

  private Other() {}

  @Override
  protected String chars() {
    return "@&%?,=[]_:-+*$#!'^~;()/.";
  }

  @Override
  public String toString() {
    return String.format("other (%s)", super.toString());
  }
}
