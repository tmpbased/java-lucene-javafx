package mpw.api;

public interface MasterKeyAlgorithm {
  public MasterKey masterKey(final MasterPassword password, final MasterKeySalt salt);
}
