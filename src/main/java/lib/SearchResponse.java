package lib;

import java.util.Collections;
import java.util.List;

public final class SearchResponse {
  public final SearchRequest request;
  public final int totalHits;
  public final List<FoundDoc> foundDocs;
  public final Throwable throwable;

  public SearchResponse(final SearchRequest request, final int totalHits, final List<FoundDoc> foundDocs) {
    this.request = request;
    this.totalHits = totalHits;
    this.foundDocs = Collections.unmodifiableList(foundDocs);
    this.throwable = null;
  }

  public SearchResponse(final SearchRequest request, final Throwable throwable) {
    this.request = request;
    this.totalHits = 0;
    this.foundDocs = Collections.emptyList();
    this.throwable = throwable;
  }
}
