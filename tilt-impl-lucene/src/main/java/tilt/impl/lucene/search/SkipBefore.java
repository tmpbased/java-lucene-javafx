package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqSkipUp;
import tilt.api.search.ApiRsSkipUp;

public final class SkipBefore extends Skip<ApiRqSkipUp, ApiRsSkipUp> {
  public SkipBefore(final IndexSearcher searcher, final ApiRqSkipUp request) {
    super(searcher, request);
  }

  @Override
  protected ApiRsSkipUp skip0(final Position position) throws IOException {
    return new ApiRsSkipUp(this.request, searchBefore(position, this.request.howMany).topDocs.scoreDocs.length);
  }

  @Override
  public ApiRsSkipUp createResponse(Throwable throwable) {
    return new ApiRsSkipUp(this.request, throwable);
  }
}