package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqSkip;
import tilt.api.search.ApiRsSkip;
import tilt.api.search.ApiToken;

public abstract class Skip<Q extends ApiRqSkip, S extends ApiRsSkip<Q>> extends Search<Q, S> {
  protected Skip(final IndexSearcher searcher, final Q request) {
    super(searcher, request);
  }

  @Override
  public final ApiToken token() {
    return this.request.token;
  }

  protected abstract S skip0(Position position) throws IOException;

  @Override
  public abstract S createResponse(Throwable throwable);

  public final S skip(Position position) {
    S response = null;
    try {
      response = skip0(position);
    } catch (final Throwable e) {
      response = createResponse(e);
    }
    return response;
  }
}