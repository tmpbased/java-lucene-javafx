package mpw.impl;

import org.bouncycastle.crypto.digests.SHA3Digest;

public class AlgorithmV1 extends Algorithm {
  public AlgorithmV1() {
    super(new Argon2MasterKey(128), new HMacSiteKey(new SHA3Digest(512)));
  }
}
