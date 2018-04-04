package tilt.impl.lucene.search.collector;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;

public abstract class ScoreDocCollector extends TopDocsCollector<ScoreDoc> {
  abstract static class ScorerLeafCollector implements LeafCollector {
    Scorer scorer;

    @Override
    public void setScorer(Scorer scorer) throws IOException {
      this.scorer = scorer;
    }
  }

  private static class SimpleScoreDocCollector extends ScoreDocCollector {
    private final boolean reversed;

    SimpleScoreDocCollector(int numHits, boolean reversed) {
      super(numHits, reversed);
      this.reversed = reversed;
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
      final int docBase = context.docBase;
      return new ScorerLeafCollector() {
        @Override
        public void collect(int doc) throws IOException {
          float score = scorer.score();
          // This collector cannot handle these scores:
          assert score != Float.NEGATIVE_INFINITY;
          assert !Float.isNaN(score);
          totalHits++;
          if (reversed != (score <= pqTop.score)) {
            // Since docs are returned in-order (i.e., increasing doc Id), a document
            // with equal score to pqTop.score cannot compete since HitQueue favors
            // documents with lower doc Ids. Therefore reject those docs too.
            return;
          }
          pqTop.doc = doc + docBase;
          pqTop.score = score;
          pqTop = pq.updateTop();
        }
      };
    }
  }

  private static class PagingScoreDocCollector extends ScoreDocCollector {
    private final ScoreDoc after;
    private final boolean reversed;
    private int collectedHits;

    PagingScoreDocCollector(int numHits, ScoreDoc after, boolean reversed) {
      super(numHits, reversed);
      this.after = after;
      this.reversed = reversed;
      this.collectedHits = 0;
    }

    @Override
    protected int topDocsSize() {
      return collectedHits < pq.size() ? collectedHits : pq.size();
    }

    @Override
    protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
      return results == null ? new TopDocs(totalHits, new ScoreDoc[0], Float.NaN)
          : new TopDocs(totalHits, results, Float.NaN);
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
      final int docBase = context.docBase;
      final int afterDoc = after.doc - context.docBase;
      return new ScorerLeafCollector() {
        @Override
        public void collect(int doc) throws IOException {
          float score = scorer.score();
          // This collector cannot handle these scores:
          assert score != Float.NEGATIVE_INFINITY;
          assert !Float.isNaN(score);
          totalHits++;
          if (reversed) {
            if (score < after.score || (score == after.score && doc >= afterDoc)) {
              // hit was collected on a previous page
              return;
            }
          } else {
            if (score > after.score || (score == after.score && doc <= afterDoc)) {
              // hit was collected on a previous page
              return;
            }
          }
          if (reversed != (score <= pqTop.score)) {
            // Since docs are returned in-order (i.e., increasing doc Id), a document
            // with equal score to pqTop.score cannot compete since HitQueue favors
            // documents with lower doc Ids. Therefore reject those docs too.
            return;
          }
          collectedHits++;
          pqTop.doc = doc + docBase;
          pqTop.score = score;
          pqTop = pq.updateTop();
        }
      };
    }
  }

  /**
   * Creates a new {@link TopScoreDocCollector} given the number of hits to
   * collect, the bottom of the previous page, and whether documents are scored in
   * order by the input {@link Scorer} to {@link LeafCollector#setScorer(Scorer)}.
   *
   * <p>
   * <b>NOTE</b>: The instances returned by this method pre-allocate a full array
   * of length <code>numHits</code>, and fill the array with sentinel objects.
   */
  public static ScoreDocCollector createAfter(int numHits, ScoreDoc after) {
    if (numHits <= 0) {
      throw new IllegalArgumentException(
          "numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
    }
    if (after == null) {
      return new SimpleScoreDocCollector(numHits, false);
    } else {
      return new PagingScoreDocCollector(numHits, after, false);
    }
  }

  public static ScoreDocCollector createBefore(int numHits, ScoreDoc before) {
    if (numHits <= 0) {
      throw new IllegalArgumentException(
          "numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
    }
    if (before == null) {
      return new SimpleScoreDocCollector(numHits, true);
    } else {
      return new PagingScoreDocCollector(numHits, before, true);
    }
  }

  ScoreDoc pqTop;

  // prevents instantiation
  ScoreDocCollector(int numHits, boolean reversed) {
    super(reversed ? new ReversedHitQueue(numHits, true) : new HitQueue(numHits, true));
    // HitQueue implements getSentinelObject to return a ScoreDoc, so we know
    // that at this point top() is already initialized.
    pqTop = pq.top();
  }

  @Override
  protected TopDocs newTopDocs(ScoreDoc[] results, int start) {
    if (results == null) {
      return EMPTY_TOPDOCS;
    }
    // We need to compute maxScore in order to set it in TopDocs. If start == 0,
    // it means the largest element is already in results, use its score as
    // maxScore. Otherwise pop everything else, until the largest element is
    // extracted and use its score as maxScore.
    float maxScore = Float.NaN;
    if (start == 0) {
      maxScore = results[0].score;
    } else {
      for (int i = pq.size(); i > 1; i--) {
        pq.pop();
      }
      maxScore = pq.pop().score;
    }
    return new TopDocs(totalHits, results, maxScore);
  }

  @Override
  public boolean needsScores() {
    return true;
  }
}
