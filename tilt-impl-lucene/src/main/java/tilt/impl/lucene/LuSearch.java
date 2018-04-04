package tilt.impl.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import tilt.api.ApiRqOpenIndex;
import tilt.api.ApiRsOpenIndex;
import tilt.api.search.ApiRqScroll;
import tilt.api.search.ApiRqScrollDown;
import tilt.api.search.ApiRqScrollUp;
import tilt.api.search.ApiRqSearch;
import tilt.api.search.ApiRqSkip;
import tilt.api.search.ApiRqSkipDown;
import tilt.api.search.ApiRqSkipUp;
import tilt.api.search.ApiRsScroll;
import tilt.api.search.ApiRsSearch;
import tilt.api.search.ApiRsSkip;
import tilt.api.search.ApiToken;
import tilt.impl.lucene.search.Position;
import tilt.impl.lucene.search.Scroll;
import tilt.impl.lucene.search.ScrollAfter;
import tilt.impl.lucene.search.ScrollBefore;
import tilt.impl.lucene.search.Skip;
import tilt.impl.lucene.search.SkipAfter;
import tilt.impl.lucene.search.SkipBefore;
import tilt.lib.receiver.Receiver;
import tilt.lib.util.Closeables;

public final class LuSearch implements Closeable {
  private final Analyzer analyzer;
  private final Closeables closeables;
  private final Map<ApiToken, Position> positions;
  private IndexReader reader;
  private IndexSearcher searcher;

  public LuSearch() {
    this.analyzer = new StandardAnalyzer();
    this.closeables = new Closeables();
    this.positions = new WeakHashMap<>();
  }

  private void respondTo(final Receiver source, final Object response) {
    if (response == null) {
      return;
    }
    source.onEvent(Receiver.none(), response);
  }

  public void open(final Receiver source, final ApiRqOpenIndex request) {
    this.closeables.close();
    try {
      final Directory directory = this.closeables.add(FSDirectory.open(request.path));
      this.reader = this.closeables.add(DirectoryReader.open(directory));
      this.searcher = new IndexSearcher(this.reader);
      respondTo(source, new ApiRsOpenIndex(request));
    } catch (final Throwable e) {
      this.searcher = null;
      this.closeables.close();
      respondTo(source, new ApiRsOpenIndex(request, e));
    }
  }

  private void ensureOpen() throws IOException {
    if (this.searcher == null) {
      throw new IOException("Directory is not open for searching");
    }
  }

  public void start(final Receiver source, final ApiRqSearch request) {
    try {
      ensureOpen();
      final ApiToken token = new ApiToken();
      final Query query;
      if (request.text.isEmpty()) {
        query = new MatchAllDocsQuery();
      } else {
        final QueryParser parser = new QueryParser("name", this.analyzer);
        query = parser.parse(request.text);
      }
      this.positions.put(token, new Position(query));
      respondTo(source, new ApiRsSearch(request, token));
    } catch (final Throwable e) {
      respondTo(source, new ApiRsSearch(request, e));
    }
  }

  private <Q extends ApiRqScroll, S extends ApiRsScroll<Q>> Position ensurePositionFor(final ApiToken token)
      throws IOException {
    final Position position = this.positions.get(token);
    if (position == null) {
      throw new IOException("Unknown position for " + token);
    }
    return position;
  }

  public void scrollUp(final Receiver source, final ApiRqScrollUp request) {
    scroll(source, new ScrollBefore(this.searcher, request));
  }

  public void scrollDown(final Receiver source, final ApiRqScrollDown request) {
    scroll(source, new ScrollAfter(this.searcher, request));
  }

  private <Q extends ApiRqScroll, S extends ApiRsScroll<Q>> void scroll(final Receiver source,
      final Scroll<Q, S> search) {
    try {
      ensureOpen();
      final Position position = ensurePositionFor(search.token());
      respondTo(source, search.scroll(position));
    } catch (final Throwable e) {
      respondTo(source, search.createResponse(e));
    }
  }

  public void skipUp(final Receiver source, final ApiRqSkipUp request) {
    skip(source, new SkipBefore(this.searcher, request));
  }

  public void skipDown(final Receiver source, final ApiRqSkipDown request) {
    skip(source, new SkipAfter(this.searcher, request));
  }

  private <Q extends ApiRqSkip, S extends ApiRsSkip<Q>> void skip(final Receiver source, final Skip<Q, S> search) {
    try {
      ensureOpen();
      final Position position = ensurePositionFor(search.token());
      respondTo(source, search.skip(position));
    } catch (final Throwable e) {
      respondTo(source, search.createResponse(e));
    }
  }

  @Override
  public void close() {
    this.positions.clear();
    this.searcher = null;
    this.closeables.close();
    if (this.analyzer != null) {
      this.analyzer.close();
    }
  }
}