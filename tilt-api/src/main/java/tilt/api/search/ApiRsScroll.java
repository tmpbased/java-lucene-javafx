package tilt.api.search;

import tilt.api.ApiRsThrows;

public abstract class ApiRsScroll<Q extends ApiRqScroll> extends ApiRsThrows<Q> {
  public final ApiDocList docs;
  public final boolean hasMoreUp, hasMoreDown;

  ApiRsScroll(final Q request, final ApiDocList docs, final boolean hasMoreUp,
      final boolean hasMoreDown) {
    super(request);
    this.docs = docs;
    this.hasMoreUp = hasMoreUp;
    this.hasMoreDown = hasMoreDown;
  }

  ApiRsScroll(final Q request, final Throwable throwable) {
    super(request, throwable);
    this.docs = ApiDocList.EMPTY;
    this.hasMoreUp = this.hasMoreDown = false;
  }
}
