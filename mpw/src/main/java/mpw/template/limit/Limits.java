package mpw.template.limit;

import java.util.Collection;
import java.util.stream.Collectors;

public final class Limits {
  private final Collection<? extends Limit> limits;

  public Limits(final Collection<? extends Limit> limits) {
    this.limits = limits;
  }

  public int getMinLength() {
    return this.limits.stream().mapToInt(Limit::getMinCount).sum(); // TODO reduce(0,
                                                                    // Math::addExact)
  }

  public int getMaxLength() {
    int maxLength = 0;
    for (final Limit limit : this.limits) {
      final int maxCount = limit.getMaxCount(Integer.MAX_VALUE);
      if (maxCount == Integer.MAX_VALUE) {
        return maxCount;
      }
      maxLength = Math.addExact(maxLength, maxCount);
    }
    return maxLength;
  }

  @Override
  public String toString() {
    return this.limits.stream().map(Limit::toString).collect(Collectors.joining(", ", "{", "}"));
  }
}
