package tilt.impl.lucene.search.collector;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;

class ReversedHitQueue extends PriorityQueue<ScoreDoc> {
  ReversedHitQueue(int size, boolean prePopulate) {
    super(size, prePopulate);
  }

  @Override
  protected ScoreDoc getSentinelObject() {
    return new ScoreDoc(Integer.MIN_VALUE, Float.POSITIVE_INFINITY);
  }

  @Override
  protected final boolean lessThan(ScoreDoc hitA, ScoreDoc hitB) {
    if (hitA.score == hitB.score)
      return hitA.doc < hitB.doc;
    else
      return hitA.score > hitB.score;
  }
}
