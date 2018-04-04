package tilt.api.index;

import java.nio.file.Path;

import tilt.api.Request;

public final class ApiRqCreateIndex implements Request {
  public final Path path;

  public ApiRqCreateIndex(final Path path) {
    this.path = path;
  }
}
