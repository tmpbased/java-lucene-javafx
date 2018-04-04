package tilt.impl.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;

import tilt.api.search.ApiRqScrollDown;
import tilt.api.search.ApiRsScrollDown;

public final class ScrollAfter extends Scroll<ApiRqScrollDown, ApiRsScrollDown> {
  public ScrollAfter(final IndexSearcher searcher, final ApiRqScrollDown request) {
    super(searcher, request);
  }

  @Override
  protected ApiRsScrollDown scroll0(final Position position) throws IOException {
    final SearchResult result = searchAfter(position, this.request.howMany);
    return new ApiRsScrollDown(this.request, createDocList(result.topDocs), result.hasMoreBefore, result.hasMoreAfter);
  }

  @Override
  public ApiRsScrollDown createResponse(Throwable throwable) {
    return new ApiRsScrollDown(this.request, throwable);
  }
}