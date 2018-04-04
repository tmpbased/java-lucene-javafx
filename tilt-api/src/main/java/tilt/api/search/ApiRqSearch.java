package tilt.api.search;

import tilt.api.Request;

public final class ApiRqSearch implements Request {
  public String text;

  public ApiRqSearch(final String text) {
    this.text = text;
  }
}
