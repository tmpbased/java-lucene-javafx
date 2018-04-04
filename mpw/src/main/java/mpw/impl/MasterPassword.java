package mpw.impl;

public final class MasterPassword extends Bytes implements mpw.api.MasterPassword {
  public MasterPassword(final byte[] bytes) {
    super(bytes);
  }

  @Override
  public String toString() {
    return String.format("MasterPassword (%s byte(s))", bytes().length);
  }
}
