package mpw.impl;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class AlgorithmV0 extends Algorithm {
  public AlgorithmV0() {
    super(new ScryptMasterKey(64), new HMacSiteKey(new SHA256Digest()));
  }
}
