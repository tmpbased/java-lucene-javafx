package lib.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

import lib.FoundDoc;
import lib.SearchRequest;
import lib.SearchResponse;
import lib.receiver.Receiver;

final class SearchSession implements Closeable {
  private static final int BATCH_SIZE = 5;

  private final IndexReader reader;
  private final Analyzer analyzer;
  private final IndexSearcher searcher;

  public SearchSession(final Directory dir) throws IOException {
    this.reader = DirectoryReader.open(dir);
    this.analyzer = new StandardAnalyzer();
    this.searcher = new IndexSearcher(this.reader);
  }

  private Stream<FoundDoc> mapToFound(final Stream<ScoreDoc> stream) {
    return stream
      .map(it -> { try { return this.searcher.doc(it.doc); } catch (final IOException e) { throw new UncheckedIOException(e); } })
      .map(FoundDoc::new);
  }

  private List<FoundDoc> take(final TopDocs topDocs, final int count) {
    final int copySize = Math.min(topDocs.scoreDocs.length, count);
    return mapToFound(Arrays.stream(topDocs.scoreDocs)).collect(Collectors.toList()).subList(0, copySize);
  }

  private SearchResponse pagingSearch(final SearchRequest request, final Query query) throws IOException {
    final ArrayList<FoundDoc> docs = new ArrayList<>(request.howMany);
    int offset = 0, totalHits = 0;
    ScoreDoc after = null;
    while (true) {
      final TopScoreDocCollector collector = TopScoreDocCollector.create(BATCH_SIZE, after);
      this.searcher.search(query, collector);
      totalHits = collector.getTotalHits();
      final int length, relativeStart = request.start - offset, left = request.howMany - docs.size();
      final TopDocs topDocs;
      if (docs.isEmpty() && relativeStart < BATCH_SIZE) {
        topDocs = collector.topDocs(relativeStart, left);
        docs.addAll(take(topDocs, left));
        length = relativeStart + topDocs.scoreDocs.length;
      } else if (!docs.isEmpty()) {
        topDocs = collector.topDocs(0, left);
        docs.addAll(take(topDocs, left));
        length = topDocs.scoreDocs.length;
      } else {
        topDocs = collector.topDocs();
        length = topDocs.scoreDocs.length;
      }
      if (docs.size() >= request.howMany) {
        break;
      }
      if (length < BATCH_SIZE) {
        break;
      }
      offset += length;
      after = topDocs.scoreDocs[topDocs.scoreDocs.length - 1];
    }
    docs.trimToSize();
    return new SearchResponse(request, totalHits, docs);
  }

  public void search(final Receiver source, final SearchRequest request) {
    SearchResponse response = null;
    try {
      final QueryParser parser = new QueryParser("name", this.analyzer);
      final Query query = parser.parse(request.text);
      if (request.start + request.howMany > BATCH_SIZE) {
        response = pagingSearch(request, query);
      } else {
        final TopScoreDocCollector collector = TopScoreDocCollector.create(BATCH_SIZE);
        this.searcher.search(query, collector);
        final TopDocs docs = collector.topDocs(request.start, request.howMany);
        final List<FoundDoc> foundDocs = mapToFound(Arrays.stream(docs.scoreDocs)).collect(Collectors.toList());
        response = new SearchResponse(request, collector.getTotalHits(), foundDocs);
      }
    } catch (final Throwable e) {
      response = new SearchResponse(request, e);
    } finally {
      if (response != null) {
        source.onEvent(Receiver.none(), response);
      }
    }
  }

  @Override
  public void close() throws IOException {
    if (this.analyzer != null) {
      this.analyzer.close();
    }
    if (this.reader != null) {
      try { this.reader.close(); } catch (final IOException e) {}
    }
  }
}