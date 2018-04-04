package tilt.api.search;

public final class ApiRsSkipUp extends ApiRsSkip<ApiRqSkipUp> {
  public ApiRsSkipUp(final ApiRqSkipUp request, final int count) {
    super(request, count);
  }

  public ApiRsSkipUp(final ApiRqSkipUp request, final Throwable throwable) {
    super(request, throwable);
  }
}
