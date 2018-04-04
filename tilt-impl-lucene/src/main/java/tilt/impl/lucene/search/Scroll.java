package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqScroll;
import tilt.api.search.ApiRsScroll;
import tilt.api.search.ApiToken;

public abstract class Scroll<Q extends ApiRqScroll, S extends ApiRsScroll<Q>> extends Search<Q, S> {
  protected Scroll(final IndexSearcher searcher, final Q request) {
    super(searcher, request);
  }

  @Override
  public final ApiToken token() {
    return this.request.token;
  }

  protected abstract S scroll0(Position position) throws IOException;

  @Override
  public abstract S createResponse(Throwable throwable);

  public final S scroll(Position position) {
    S response = null;
    try {
      response = scroll0(position);
    } catch (final Throwable e) {
      response = createResponse(e);
    }
    return response;
  }
}