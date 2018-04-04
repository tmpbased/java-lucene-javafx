package tilt.impl.lucene;

import tilt.api.ApiRqOpenIndex;
import tilt.api.Tilt;
import tilt.api.index.ApiRqCreateIndex;
import tilt.api.search.ApiRqScrollDown;
import tilt.api.search.ApiRqScrollUp;
import tilt.api.search.ApiRqSearch;
import tilt.api.search.ApiRqSkipDown;
import tilt.api.search.ApiRqSkipUp;
import tilt.lib.receiver.RcvChannel;
import tilt.lib.receiver.RcvGeneric;
import tilt.lib.receiver.RcvGenericBinding;
import tilt.lib.receiver.RcvStaticBindings;
import tilt.lib.receiver.Receiver;
import tilt.lib.util.Closeables;

public final class Lucene implements Tilt {
  private final Closeables luceneCloseables;
  private final Receiver localReceiver, remoteReceiver;

  public Lucene(final Receiver receiver) {
    this.remoteReceiver = receiver;
    this.luceneCloseables = new Closeables();
    final LuSearch searcher = this.luceneCloseables.add(new LuSearch());
    final LuIndex indexer = this.luceneCloseables.add(new LuIndex());
    this.localReceiver = new RcvStaticBindings()
        .add(new RcvGenericBinding(ApiRqOpenIndex.class, RcvGeneric.sequence(searcher::open, indexer::open)))
        .add(new RcvGenericBinding(ApiRqCreateIndex.class, indexer::index))
        .add(new RcvGenericBinding(ApiRqSearch.class, searcher::start))
        .add(new RcvGenericBinding(ApiRqScrollUp.class, searcher::scrollUp))
        .add(new RcvGenericBinding(ApiRqScrollDown.class, searcher::scrollDown))
        .add(new RcvGenericBinding(ApiRqSkipUp.class, searcher::skipUp))
        .add(new RcvGenericBinding(ApiRqSkipDown.class, searcher::skipDown)).build();
  }

  @Override
  public void onEvent(Receiver source, Object event) {
    this.localReceiver.onEvent(new RcvChannel(this, source), event);
  }

  @Override
  public void close() {
    this.luceneCloseables.close();
  }
}
