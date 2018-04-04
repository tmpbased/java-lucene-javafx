package tilt.api.search;

import java.util.Collections;
import java.util.List;

public final class ApiDocList {
  public static final ApiDocList EMPTY = new ApiDocList();

  public final List<ApiDoc> docs;

  private ApiDocList() {
    this.docs = Collections.emptyList();
  }

  public ApiDocList(final List<ApiDoc> docs) {
    this.docs = Collections.unmodifiableList(docs);
  }
}
