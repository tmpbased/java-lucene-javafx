package tilt.impl.lucene;

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
import org.apache.lucene.store.FSDirectory;

import tilt.api.ApiRqOpenIndex;
import tilt.api.ApiRsOpenIndex;
import tilt.api.index.ApiRqCreateIndex;
import tilt.api.index.ApiRsCreateIndex;
import tilt.lib.receiver.Receiver;
import tilt.lib.util.Closeables;

public final class LuIndex implements Closeable {
  private final Analyzer analyzer;
  private final Closeables closeables;
  private IndexWriter writer;

  public LuIndex() {
    this.analyzer = new StandardAnalyzer();
    this.closeables = new Closeables();
  }

  public void open(final Receiver source, final ApiRqOpenIndex request) {
    this.closeables.close();
    ApiRsOpenIndex response = null;
    try {
      final IndexWriterConfig iwc = new IndexWriterConfig(this.analyzer);
      iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      final Directory directory = this.closeables.add(FSDirectory.open(request.path));
      this.writer = this.closeables.add(new IndexWriter(directory, iwc));
      response = new ApiRsOpenIndex(request);
    } catch (final Throwable e) {
      response = new ApiRsOpenIndex(request, e);
      this.writer = null;
      this.closeables.close();
    } finally {
      if (response != null) {
        source.onEvent(Receiver.none(), response);
      }
    }
  }

  public void index(final Receiver source, final ApiRqCreateIndex request) {
    ApiRsCreateIndex response = null;
    try {
      ensureOpen();
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
      response = new ApiRsCreateIndex(request);
    } catch (final Throwable e) {
      response = new ApiRsCreateIndex(request, e);
    } finally {
      if (response != null) {
        source.onEvent(Receiver.none(), response);
      }
    }
  }

  private void ensureOpen() throws IOException {
    if (this.writer == null) {
      throw new IOException("Directory is not open for indexing");
    }
  }

  private void indexDoc(final Path dir, final Path file) throws IOException {
    final Document doc = new Document();
    // TODO current timestamp
    doc.add(new StringField("path", file.toString(), Field.Store.YES));
    doc.add(new LongPoint("size", Files.size(file)));
    // TODO hash(es)
    doc.add(new LongPoint("modified", Files.getLastModifiedTime(file).toMillis()));
    doc.add(new TextField("name", dir.relativize(file).toString(), Field.Store.NO));
    this.writer.updateDocument(new Term("path", file.toString()), doc);
  }

  @Override
  public void close() {
    this.writer = null;
    this.closeables.close();
    this.analyzer.close();
  }
}
