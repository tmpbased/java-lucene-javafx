package lib.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import lib.IndexRequest;
import lib.IndexResponse;
import lib.receiver.Receiver;

final class IndexSession implements Closeable {
  private final Analyzer analyzer;
  private final IndexWriter writer;

  public IndexSession(final Directory dir) throws IOException {
    this.analyzer = new StandardAnalyzer();
    final IndexWriterConfig iwc = new IndexWriterConfig(this.analyzer);
    iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
    this.writer = new IndexWriter(dir, iwc);
  }

  public void index(final Receiver source, final IndexRequest request) {
    IndexResponse response = null;
    try {
      if (Files.isDirectory(request.path)) {
        Files.walkFileTree(request.path, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            indexDoc(request.path, file);
            return FileVisitResult.CONTINUE;
          }
        });
      } else {
        indexDoc(request.path.getParent(), request.path);
      }
      response = new IndexResponse(request);
    } catch (final Throwable e) {
      response = new IndexResponse(request, e);
    } finally {
      if (response != null) {
        source.onEvent(Receiver.none(), response);
      }
    }
  }

  private void indexDoc(final Path dir, final Path file) throws IOException {
    final Document doc = new Document();
    doc.add(new StringField("path", file.toString(), Field.Store.YES));
    doc.add(new LongPoint("size", Files.size(file)));
    // TODO hash(es)
    doc.add(new LongPoint("modified", Files.getLastModifiedTime(file).toMillis()));
    doc.add(new TextField("name", dir.relativize(file).toString(), Field.Store.NO));
    this.writer.updateDocument(new Term("path", file.toString()), doc);
  }

  @Override
  public void close() throws IOException {
    if (this.writer != null) {
      try { this.writer.close(); } catch (final IOException e) {}
    }
    if (this.analyzer != null) {
      this.analyzer.close();
    }
  }
}
