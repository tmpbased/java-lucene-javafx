package tilt.impl.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

import tilt.api.search.ApiDoc;

final class LuDoc implements ApiDoc {
  final ScoreDoc scoreDoc;
  final Document doc;

  LuDoc(final ScoreDoc scoreDoc, final Document doc) {
    this.scoreDoc = scoreDoc;
    this.doc = doc;
  }

  @Override
  public String getPath() {
    return this.doc.get("path");
  }

  @Override
  public double getRelevance() {
    return this.scoreDoc.doc;
  }
}
