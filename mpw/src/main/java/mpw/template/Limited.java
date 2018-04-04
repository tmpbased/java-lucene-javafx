package mpw.template;

import java.util.Objects;

import mpw.template.limit.Limit;

final class Limited<T> extends Limit {
  private final T value;
  private final Limit limit;

  public Limited(final T value, final Limit limit) {
    this.value = value;
    this.limit = Objects.requireNonNull(limit,
        () -> String.format("LimitedValue(%s, %s): limit == null", value, limit));
  }

  @Override
  public int getMinCount() {
    return this.limit.getMinCount();
  }

  @Override
  public int getMaxCount(int maxCount) {
    return this.limit.getMaxCount(maxCount);
  }

  @Override
  public boolean matches(int count) {
    return this.limit.matches(count);
  }

  @Override
  public String toString() {
    return String.format("%s ×%s", this.value, this.limit);
  }
}
