package lib;

public class IndexResponse {
  public final IndexRequest request;
  public final Throwable throwable;

  public IndexResponse(final IndexRequest request) {
    this.request = request;
    this.throwable = null;
  }

  public IndexResponse(final IndexRequest request, final Throwable throwable) {
    this.request = request;
    this.throwable = throwable;
  }
}
