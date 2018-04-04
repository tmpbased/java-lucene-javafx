package mpw.lang;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Mappers {
  private Mappers() {}

  public static <K, V, T extends Map.Entry<K, V>, R> Function<T, R> destructMapEntry(
      final BiFunction<? super K, ? super V, ? extends R> destructor) {
    return it -> destructor.apply(it.getKey(), it.getValue());
  }
}
