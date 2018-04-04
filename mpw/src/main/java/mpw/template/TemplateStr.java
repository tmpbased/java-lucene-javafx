package mpw.template;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class TemplateStr {
  private final Abbrev abbrev;
  private final String str;

  public TemplateStr(final Abbrev abbrev, final String str) {
    str.chars().forEach(it -> abbrev.expand((char) it));
    this.abbrev = abbrev;
    this.str = str;
  }

  private static Collector<Character, ?, String> collectToString() {
    return Collector.of(() -> new StringBuilder(), (sb, ch) -> sb.append(ch), (a, b) -> {
      a.append(b);
      return a;
    }, StringBuilder::toString);
  }

  public TemplateStr(final Abbrev abbrev, final Template template) {
    this.abbrev = abbrev;
    this.str = template.stream().map(it -> abbrev.abbreviate(it)).collect(collectToString());
  }

  public Template toTemplate() {
    return new Letters(this.str.chars().mapToObj(it -> this.abbrev.expand((char) it))
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public String toString() {
    return this.str;
  }
}
