package mpw.letter;

import java.util.List;

abstract class JoinedStr extends AnyCharOfStr {
  private final List<? extends AnyCharOfStr> classes;

  public JoinedStr(final List<? extends AnyCharOfStr> classes) {
    this.classes = classes;
  }

  @Override
  protected String chars() {
    final StringBuilder sb = new StringBuilder();
    for (final AnyCharOfStr charClass : this.classes) {
      sb.append(charClass.chars());
    }
    return sb.toString();
  }
}
