package tilt.impl.lucene.search;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import tilt.api.Request;
import tilt.api.Response;
import tilt.api.search.ApiDoc;
import tilt.api.search.ApiDocList;
import tilt.api.search.ApiToken;
import tilt.impl.lucene.search.collector.ScoreDocCollector;

public abstract class Search<Q extends Request, S extends Response<Q>> {
  private final IndexSearcher searcher;
  protected final Q request;

  protected Search(final IndexSearcher searcher, final Q request) {
    this.searcher = searcher;
    this.request = request;
  }

  public abstract ApiToken token();

  public abstract S createResponse(Throwable throwable);

  protected static final class SearchResult {
    public final TopDocs topDocs;
    public final boolean hasMoreBefore, hasMoreAfter;

    public SearchResult(final TopDocs topDocs, final boolean hasMoreBefore, final boolean hasMoreAfter) {
      this.topDocs = topDocs;
      this.hasMoreBefore = hasMoreBefore;
      this.hasMoreAfter = hasMoreAfter;
    }
  }

  protected final ApiDoc createDoc(final ScoreDoc scoreDoc) {
    try {
      final Document doc = this.searcher.doc(scoreDoc.doc);
      return new LuDoc(scoreDoc, doc);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected final ApiDocList createDocList(final TopDocs topDocs) {
    final List<ApiDoc> docList = Arrays.stream(topDocs.scoreDocs).map(this::createDoc).collect(Collectors.toList());
    return new ApiDocList(docList);
  }

  private <T extends Collector> T search(final Query query, final T collector) throws IOException {
    this.searcher.search(query, collector);
    return collector;
  }

  protected final SearchResult searchBefore(final Position position, final int numHits) throws IOException {
    assert numHits > 0;
    final TopDocs beforeDocs = search(position.query, ScoreDocCollector.createBefore(numHits + 1, position.before()))
        .topDocs();
    final int collectedHits = beforeDocs.scoreDocs.length;
    final boolean hasMoreBefore = collectedHits > numHits, hasMoreAfter = position.after() != null;
    if (hasMoreBefore) {
      position.moveBefore(beforeDocs.scoreDocs[collectedHits - 2], beforeDocs.scoreDocs[collectedHits - 1]);
    } else if (collectedHits > 0) {
      position.moveBefore(beforeDocs.scoreDocs[collectedHits - 1], null);
    }
    final ScoreDoc[] scoreDocs = hasMoreBefore ? Arrays.copyOf(beforeDocs.scoreDocs, collectedHits - 1)
        : beforeDocs.scoreDocs;
    Collections.reverse(Arrays.asList(scoreDocs));
    return new SearchResult(new TopDocs(beforeDocs.totalHits, scoreDocs, beforeDocs.getMaxScore()), hasMoreBefore,
        hasMoreAfter);
  }

  protected final SearchResult searchAfter(final Position position, final int numHits) throws IOException {
    final TopDocs afterDocs = search(position.query, ScoreDocCollector.createAfter(numHits + 1, position.after()))
        .topDocs();
    final int collectedHits = afterDocs.scoreDocs.length;
    final boolean hasMoreBefore = position.before() != null, hasMoreAfter = collectedHits > numHits;
    if (hasMoreAfter) {
      assert collectedHits >= 2;
      position.moveAfter(afterDocs.scoreDocs[collectedHits - 2], afterDocs.scoreDocs[collectedHits - 1]);
    } else if (collectedHits > 0) {
      position.moveAfter(afterDocs.scoreDocs[collectedHits - 1], null);
    }
    final TopDocs topDocs = hasMoreAfter
        ? new TopDocs(afterDocs.totalHits, Arrays.copyOf(afterDocs.scoreDocs, collectedHits - 1),
            afterDocs.getMaxScore())
        : afterDocs;
    return new SearchResult(topDocs, hasMoreBefore, hasMoreAfter);
  }
}