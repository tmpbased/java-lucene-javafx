package mpw.impl;

import org.bouncycastle.crypto.generators.SCrypt;

import mpw.api.MasterKeyAlgorithm;
import mpw.api.MasterKeySalt;

public class ScryptMasterKey implements MasterKeyAlgorithm {
  private final int keySize;

  public ScryptMasterKey(final int keySize) {
    this.keySize = keySize;
  }

  @Override
  public MasterKey masterKey(final mpw.api.MasterPassword password, final MasterKeySalt salt) {
    final byte[] bytes = SCrypt.generate(password.bytes(), salt.bytes(), 32768, 8, 2, this.keySize);
    return new MasterKey(bytes);
  }
}
