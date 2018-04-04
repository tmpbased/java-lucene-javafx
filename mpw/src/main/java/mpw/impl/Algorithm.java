package mpw.impl;

import mpw.api.MasterKey;
import mpw.api.MasterKeyAlgorithm;
import mpw.api.MasterKeySalt;
import mpw.api.MasterPassword;
import mpw.api.SiteKey;
import mpw.api.SiteKeyAlgorithm;
import mpw.api.SiteKeySalt;

abstract class Algorithm implements mpw.api.Algorithm {
  private final MasterKeyAlgorithm masterKeys;
  private final SiteKeyAlgorithm siteKeys;

  protected Algorithm(final MasterKeyAlgorithm masterKeys, final SiteKeyAlgorithm siteKeys) {
    this.masterKeys = masterKeys;
    this.siteKeys = siteKeys;
  }

  @Override
  public MasterKey masterKey(final MasterPassword masterPassword, final MasterKeySalt salt) {
    return this.masterKeys.masterKey(masterPassword, salt);
  }

  @Override
  public SiteKey siteKey(final MasterKey masterKey, final SiteKeySalt salt) {
    return this.siteKeys.siteKey(masterKey, salt);
  }
}
