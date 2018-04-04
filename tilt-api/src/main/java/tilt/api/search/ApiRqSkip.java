package tilt.api.search;

import tilt.api.Request;

public abstract class ApiRqSkip implements Request {
  public final ApiToken token;
  public final int howMany;

  ApiRqSkip(final ApiToken token, final int howMany) {
    this.token = token;
    this.howMany = howMany;
  }
}
