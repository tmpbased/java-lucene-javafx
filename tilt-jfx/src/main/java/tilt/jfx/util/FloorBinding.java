package tilt.jfx.util;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class FloorBinding extends DoubleBinding {
  private final ObservableNumberValue op;

  public FloorBinding(final ObservableNumberValue op) {
    this.op = op;
    super.bind(op);
  }

  @Override
  public void dispose() {
    super.unbind(this.op);
  }

  @Override
  protected double computeValue() {
    return Math.floor(this.op.doubleValue());
  }

  @Override
  public ObservableList<?> getDependencies() {
    return FXCollections.singletonObservableList(this.op);
  }
}
