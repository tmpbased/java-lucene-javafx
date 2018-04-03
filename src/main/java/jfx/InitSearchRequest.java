package jfx;

final class InitSearchRequest {
  public String text;
  public int howMany;

  public InitSearchRequest(final String text, final int howMany) {
    this.text = text;
    this.howMany = howMany;
  }
}
