package tilt.impl.lucene.search.collector;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;

class HitQueue extends PriorityQueue<ScoreDoc> {
  HitQueue(int size, boolean prePopulate) {
    super(size, prePopulate);
  }

  @Override
  protected ScoreDoc getSentinelObject() {
    return new ScoreDoc(Integer.MAX_VALUE, Float.NEGATIVE_INFINITY);
  }

  @Override
  protected final boolean lessThan(ScoreDoc hitA, ScoreDoc hitB) {
    if (hitA.score == hitB.score)
      return hitA.doc > hitB.doc;
    else
      return hitA.score < hitB.score;
  }
}
