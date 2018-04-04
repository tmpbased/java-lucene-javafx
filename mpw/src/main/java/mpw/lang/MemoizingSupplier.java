package mpw.lang;

import java.util.function.Supplier;

public final class MemoizingSupplier<T> implements Supplier<T> {
  private final Supplier<T> supplier;
  private boolean initialized;
  private T value;

  public MemoizingSupplier(final Supplier<T> supplier) {
    this.supplier = supplier;
  }

  @Override
  public T get() {
    if (this.initialized) {
      return this.value;
    }
    final T value = this.supplier.get();
    this.value = value;
    this.initialized = true;
    return value;
  }
}
