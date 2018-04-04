package tilt.api.index;

import tilt.api.ApiRsThrows;

public final class ApiRsCreateIndex extends ApiRsThrows<ApiRqCreateIndex> {
  public ApiRsCreateIndex(final ApiRqCreateIndex request) {
    super(request);
  }

  public ApiRsCreateIndex(final ApiRqCreateIndex request, final Throwable throwable) {
    super(request, throwable);
  }
}
