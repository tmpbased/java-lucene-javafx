package tilt.api.search;

import tilt.api.ApiRsThrows;

public abstract class ApiRsSkip<Q extends ApiRqSkip> extends ApiRsThrows<Q> {
  public final int count;

  ApiRsSkip(final Q request, final int count) {
    super(request);
    this.count = count;
  }

  ApiRsSkip(final Q request, final Throwable throwable) {
    super(request, throwable);
    this.count = 0;
  }
}
