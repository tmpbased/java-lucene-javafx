package mpw.api;

public interface SiteKeyAlgorithm {
  public SiteKey siteKey(final MasterKey masterKey, final SiteKeySalt salt);
}
