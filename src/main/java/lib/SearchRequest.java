package lib;

public final class SearchRequest {
  public String text;
  public int start, howMany;

  public SearchRequest(final String text, final int start, final int howMany) {
    this.text = text;
    this.start = start;
    this.howMany = howMany;
  }
}
