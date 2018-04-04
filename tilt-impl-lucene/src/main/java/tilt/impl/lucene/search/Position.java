package tilt.impl.lucene.search;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public final class Position {
  public final Query query;
  private ScoreDoc before;
  private ScoreDoc after;

  public Position(final Query query) {
    this.query = query;
  }

  public ScoreDoc after() {
    return after;
  }

  public ScoreDoc before() {
    return before;
  }

  public void moveBefore(final ScoreDoc first, final ScoreDoc beforeFirst) {
    this.before = first;
    this.after = beforeFirst;
  }

  public void moveAfter(final ScoreDoc last, final ScoreDoc afterLast) {
    this.after = last;
    this.before = afterLast;
  }
}