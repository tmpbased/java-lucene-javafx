package tilt.api;

import java.nio.file.Path;

public final class ApiRqOpenIndex implements Request {
  public final Path path;

  public ApiRqOpenIndex(final Path path) {
    this.path = path;
  }
}
