package lib;

import org.apache.lucene.document.Document;

public class FoundDoc {
  public final String path;

  public FoundDoc(final Document doc) {
    this.path = doc.get("path");
  }
}
