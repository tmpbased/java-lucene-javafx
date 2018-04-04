package tilt.jfx.util;

import javafx.beans.binding.LongBinding;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ToLongBinding extends LongBinding {
  private final ObservableNumberValue op;

  public ToLongBinding(final ObservableNumberValue op) {
    this.op = op;
    super.bind(op);
  }

  @Override
  public void dispose() {
    super.unbind(this.op);
  }

  @Override
  protected long computeValue() {
    return Math.round(this.op.doubleValue());
  }

  @Override
  public ObservableList<?> getDependencies() {
    return FXCollections.singletonObservableList(this.op);
  }
}
