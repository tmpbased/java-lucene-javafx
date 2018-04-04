package tilt.api.search;

public final class ApiRsSkipDown extends ApiRsSkip<ApiRqSkipDown> {
  public ApiRsSkipDown(final ApiRqSkipDown request, final int count) {
    super(request, count);
  }

  public ApiRsSkipDown(final ApiRqSkipDown request, final Throwable throwable) {
    super(request, throwable);
  }
}
