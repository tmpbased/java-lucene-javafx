package mpw.impl;

abstract class Bytes {
  private final byte[] bytes;

  public Bytes(final byte[] bytes) {
    this.bytes = bytes;
  }

  public byte[] bytes() {
    return this.bytes;
  }
}
