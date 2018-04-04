package mpw.impl;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import mpw.api.MasterKeyAlgorithm;
import mpw.api.MasterKeySalt;
import mpw.api.MasterPassword;

public class Argon2MasterKey implements MasterKeyAlgorithm {
  private static final String KEY = "9866540c-8620-4d0e-b21a-346d7e2749a6";
  private static final int ARGON2_MEMORY_KB = 1 << 22; // 4GiB

  private final int keySize;

  public Argon2MasterKey(final int keySize) {
    this.keySize = keySize;
  }

  @Override
  public MasterKey masterKey(MasterPassword password, MasterKeySalt salt) {
    final var bytes = new byte[this.keySize];
    final var params = new Argon2Parameters.Builder().withVersion(Argon2Parameters.ARGON2_id)
        .withIterations(4).withMemoryAsKB(ARGON2_MEMORY_KB).withParallelism(8)
        .withSalt(salt.bytes()).withAdditional(KEY.getBytes(StandardCharsets.US_ASCII));
    final var argon2 = new Argon2BytesGenerator();
    argon2.init(params.build());
    argon2.generateBytes(password.bytes(), bytes);
    return new MasterKey(bytes);
  }
}
