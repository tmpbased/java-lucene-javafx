package tilt.api.search;

public final class ApiRsScrollDown extends ApiRsScroll<ApiRqScrollDown> {
  public ApiRsScrollDown(final ApiRqScrollDown request, final ApiDocList docs,
      final boolean hasMoreUp, final boolean hasMoreDown) {
    super(request, docs, hasMoreUp, hasMoreDown);
  }

  public ApiRsScrollDown(final ApiRqScrollDown request, final Throwable throwable) {
    super(request, throwable);
  }
}
