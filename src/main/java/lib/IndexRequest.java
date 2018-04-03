package lib;

import java.nio.file.Path;

public class IndexRequest {
  public final Path path;

  public IndexRequest(final Path path) {
    this.path = path;
  }
}
