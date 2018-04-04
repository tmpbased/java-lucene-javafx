package tilt.api;

public interface Response<Q extends Request> {
  Q request();
}
