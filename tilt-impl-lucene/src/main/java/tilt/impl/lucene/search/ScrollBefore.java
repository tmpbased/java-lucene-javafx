package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqScrollUp;
import tilt.api.search.ApiRsScrollUp;

public final class ScrollBefore extends Scroll<ApiRqScrollUp, ApiRsScrollUp> {
  public ScrollBefore(final IndexSearcher searcher, final ApiRqScrollUp request) {
    super(searcher, request);
  }

  @Override
  protected ApiRsScrollUp scroll0(final Position position) throws IOException {
    final SearchResult result = searchBefore(position, this.request.howMany);
    return new ApiRsScrollUp(this.request, createDocList(result.topDocs), result.hasMoreBefore, result.hasMoreAfter);
  }

  @Override
  public ApiRsScrollUp createResponse(Throwable throwable) {
    return new ApiRsScrollUp(this.request, throwable);
  }
}