package mpw.template.weight;

import java.util.function.Predicate;

public abstract class Weight {
  /**
   * @return finite, non-negative value.
   */
  public abstract double weight(Weightable weightable);

  public static final class Fixed extends Weight {
    public static final Fixed ZERO, ONE;

    static {
      ZERO = new Fixed(0d);
      ONE = new Fixed(1d);
    }

    private final double weight;

    public Fixed(final double weight) {
      this.weight = weight;
    }

    @Override
    public double weight(final Weightable weightable) {
      return this.weight;
    }
  }

  protected abstract static class IfThenElse extends Weight {
    private final Predicate<Weightable> ifClause;
    private final Weight thenWeight, elseWeight;

    protected IfThenElse(final Predicate<Weightable> ifClause, final Weight thenWeight,
        final Weight elseWeight) {
      this.ifClause = ifClause;
      this.thenWeight = thenWeight;
      this.elseWeight = elseWeight;
    }

    @Override
    public double weight(Weightable weightable) {
      return (this.ifClause.test(weightable) ? this.thenWeight : this.elseWeight)
          .weight(weightable);
    }
  }

  public static final class CappedRun extends IfThenElse {
    public CappedRun(final int max, final Weight weight) {
      super(weightable -> weightable.runLength(weightable.letter()) >= max, Fixed.ZERO, weight);
    }
  }
}
