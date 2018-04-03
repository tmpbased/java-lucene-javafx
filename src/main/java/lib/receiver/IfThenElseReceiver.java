package lib.receiver;

import java.util.function.Predicate;

public class IfThenElseReceiver<E> implements GenericReceiver<E> {
  private final Predicate<E> ifClause;
  private final Receiver thenClause, elseClause;

  public IfThenElseReceiver(final Predicate<E> ifClause, final Receiver thenClause, final Receiver elseClause) {
    this.ifClause = ifClause;
    this.thenClause = thenClause;
    this.elseClause = elseClause;
  }

  @Override
  public void onEvent(Receiver source, E event) {
    if (this.ifClause.test(event)) {
      this.thenClause.onEvent(source, event);
    } else {
      this.elseClause.onEvent(source, event);
    }
  }
}
