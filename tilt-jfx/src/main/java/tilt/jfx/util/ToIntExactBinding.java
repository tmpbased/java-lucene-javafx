package tilt.jfx.util;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableLongValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ToIntExactBinding extends IntegerBinding {
  private final ObservableLongValue op;

  public ToIntExactBinding(final ObservableLongValue op) {
    this.op = op;
    super.bind(op);
  }

  @Override
  public void dispose() {
    super.unbind(this.op);
  }

  @Override
  protected int computeValue() {
    return Math.toIntExact(this.op.get());
  }

  @Override
  public ObservableList<?> getDependencies() {
    return FXCollections.singletonObservableList(this.op);
  }
}
