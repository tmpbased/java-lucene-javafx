package mpw.impl;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import mpw.api.MasterKey;
import mpw.api.SiteKeyAlgorithm;
import mpw.api.SiteKeySalt;

public class HMacSiteKey implements SiteKeyAlgorithm {
  private final Mac mac;

  public HMacSiteKey(final Digest digest) {
    this.mac = new HMac(digest);
  }

  @Override
  public SiteKey siteKey(MasterKey masterKey, SiteKeySalt salt) {
    this.mac.init(new KeyParameter(masterKey.bytes()));
    write(salt.bytes());
    return new SiteKey(bytes());
  }

  private void write(final byte[] bytes) {
    this.mac.update(bytes, 0, bytes.length);
  }

  private byte[] bytes() {
    final var bytes = new byte[this.mac.getMacSize()];
    this.mac.doFinal(bytes, 0);
    return bytes;
  }
}
