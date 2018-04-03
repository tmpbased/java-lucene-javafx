package lib.receiver;

import java.util.function.Function;

public class ReplaceEventReceiver<E, T> implements GenericReceiver<E> {
  private final Receiver receiver;
  private final Function<E, T> mapClause;

  public ReplaceEventReceiver(final Receiver receiver, final Function<E, T> mapClause) {
    this.receiver = receiver;
    this.mapClause = mapClause;
  }

  @Override
  public void onEvent(Receiver source, E event) {
    this.receiver.onEvent(source, this.mapClause.apply(event));
  }
}
