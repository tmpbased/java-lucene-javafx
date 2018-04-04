package tilt.api.search;

import tilt.api.ApiRsThrows;

public final class ApiRsSearch extends ApiRsThrows<ApiRqSearch> {
  public final ApiToken token;

  public ApiRsSearch(final ApiRqSearch request, final ApiToken token) {
    super(request);
    this.token = token;
  }

  public ApiRsSearch(final ApiRqSearch request, final Throwable throwable) {
    super(request, throwable);
    this.token = null;
  }
}
