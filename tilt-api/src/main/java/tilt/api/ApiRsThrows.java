package tilt.api;

public abstract class ApiRsThrows<Q extends Request> implements Response<Q> {
  public final Q request;
  public final Throwable throwable;

  public ApiRsThrows(final Q request) {
    this.request = request;
    this.throwable = null;
  }

  public ApiRsThrows(final Q request, final Throwable throwable) {
    this.request = request;
    this.throwable = throwable;
  }

  @Override
  public Q request() {
    return this.request;
  }
}
