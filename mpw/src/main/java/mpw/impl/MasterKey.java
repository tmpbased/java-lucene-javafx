package mpw.impl;

final class MasterKey extends Bytes implements mpw.api.MasterKey {
  public MasterKey(final byte[] bytes) {
    super(bytes);
  }

  @Override
  public String toString() {
    return String.format("MasterKey (%s byte(s))", bytes().length);
  }
}
