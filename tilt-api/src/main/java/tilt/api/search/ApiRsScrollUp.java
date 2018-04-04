package tilt.api.search;

public final class ApiRsScrollUp extends ApiRsScroll<ApiRqScrollUp> {
  public ApiRsScrollUp(final ApiRqScrollUp request, final ApiDocList docs, final boolean hasMoreUp,
      final boolean hasMoreDown) {
    super(request, docs, hasMoreUp, hasMoreDown);
  }

  public ApiRsScrollUp(final ApiRqScrollUp request, final Throwable throwable) {
    super(request, throwable);
  }
}
