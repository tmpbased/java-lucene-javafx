package mpw.template.weight;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import mpw.letter.Letter;
import mpw.template.limit.Limit;

public interface Weightable {
  Letter letter();

  Limit limit(Letter letter);

  List<Letter> letters();

  default int length(Letter letter) {
    return Math.toIntExact(letters().stream().filter(Predicate.isEqual(letter)).count());
  }

  default int runLength(Letter letter) {
    final List<Letter> letters = letters();
    final ListIterator<Letter> it = letters.listIterator(letters.size());
    int run = 0;
    while (it.hasPrevious()) {
      if (it.previous() == letter) {
        run++;
      } else {
        break;
      }
    }
    return run;
  }
}
