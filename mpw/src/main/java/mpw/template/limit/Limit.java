package mpw.template.limit;

public abstract class Limit {
  protected Limit() {}

  public abstract int getMinCount();

  public int getMaxCount(final int maxCount) {
    return getMinCount();
  }

  public boolean matches(final int count) {
    return count >= getMinCount() && count <= getMaxCount(Integer.MAX_VALUE);
  }

  @Override
  public String toString() {
    return String.format("[%s..%s]", getMinCount(), getMaxCount(Integer.MAX_VALUE));
  }

  public static final class Exactly extends Limit {
    private static int ensureCount(final int count) {
      if (count <= 0) {
        throw new IllegalArgumentException(String.format("Exactly(%s): count <= 0", count));
      }
      return count;
    }

    protected final int count;

    public Exactly(final int count) {
      this.count = ensureCount(count);
    }

    @Override
    public int getMinCount() {
      return this.count;
    }

    @Override
    public int getMaxCount(final int maxCount) {
      if (this.count > maxCount) {
        throw new IllegalArgumentException(
            String.format("Exactly(%s): getMaxCount(%s)", this.count, maxCount));
      }
      return this.count;
    }

    @Override
    public boolean matches(final int count) {
      return count == this.count;
    }

    @Override
    public String toString() {
      return Integer.toString(this.count);
    }
  }

  public static final class AtLeast extends Limit {
    private static int ensureMinCount(final int minCount) {
      if (minCount <= 0) {
        throw new IllegalArgumentException(String.format("AtLeast(%s): minCount <= 0", minCount));
      }
      return minCount;
    }

    protected final int minCount;

    public AtLeast(final int minCount) {
      this.minCount = ensureMinCount(minCount);
    }

    @Override
    public int getMinCount() {
      return this.minCount;
    }

    @Override
    public int getMaxCount(final int maxCount) {
      return Math.max(this.minCount, maxCount);
    }

    @Override
    public boolean matches(final int count) {
      return count >= this.minCount;
    }

    @Override
    public String toString() {
      return String.format("[%s..∞]", this.minCount);
    }
  }

  public static final class AtMost extends Limit {
    private static int ensureMaxCount(final int maxCount) {
      if (maxCount <= 0) {
        throw new IllegalArgumentException(String.format("AtMost(%s): maxCount <= 0", maxCount));
      }
      return maxCount;
    }

    protected final int maxCount;

    public AtMost(final int maxCount) {
      this.maxCount = ensureMaxCount(maxCount);
    }

    @Override
    public int getMinCount() {
      return 0;
    }

    @Override
    public int getMaxCount(final int maxCount) {
      return Math.min(this.maxCount, maxCount);
    }

    @Override
    public boolean matches(final int count) {
      return count <= this.maxCount;
    }

    @Override
    public String toString() {
      return String.format("[0..%s]", this.maxCount);
    }
  }

  public static final class Between extends Limit {
    private static int ensureMinCount(final int minCount, final int maxCount) {
      if (minCount < 0) {
        throw new IllegalArgumentException(
            String.format("Between(%s, %s): minCount < 0", minCount, maxCount));
      }
      return minCount;
    }

    private static int ensureMaxCount(final int minCount, final int maxCount) {
      if (minCount > maxCount) {
        throw new IllegalArgumentException(
            String.format("Between(%s, %s): minCount > maxCount", minCount, maxCount));
      }
      return maxCount;
    }

    protected final int minCount, maxCount;

    public Between(final int minCount, final int maxCount) {
      this.minCount = ensureMinCount(minCount, maxCount);
      this.maxCount = ensureMaxCount(minCount, maxCount);
    }

    @Override
    public int getMinCount() {
      return this.minCount;
    }

    @Override
    public int getMaxCount(final int maxCount) {
      return Math.max(this.minCount, Math.min(this.maxCount, maxCount));
    }

    @Override
    public boolean matches(final int count) {
      return count >= this.minCount && count <= this.maxCount;
    }

    @Override
    public String toString() {
      return String.format("[%s..%s]", this.minCount, this.maxCount);
    }
  }
}
