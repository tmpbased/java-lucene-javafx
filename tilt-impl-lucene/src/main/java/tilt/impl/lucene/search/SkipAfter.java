package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqSkipDown;
import tilt.api.search.ApiRsSkipDown;

public final class SkipAfter extends Skip<ApiRqSkipDown, ApiRsSkipDown> {
  public SkipAfter(final IndexSearcher searcher, final ApiRqSkipDown request) {
    super(searcher, request);
  }

  @Override
  protected ApiRsSkipDown skip0(final Position position) throws IOException {
    return new ApiRsSkipDown(this.request, searchAfter(position, this.request.howMany).topDocs.scoreDocs.length);
  }

  @Override
  public ApiRsSkipDown createResponse(Throwable throwable) {
    return new ApiRsSkipDown(this.request, throwable);
  }
}