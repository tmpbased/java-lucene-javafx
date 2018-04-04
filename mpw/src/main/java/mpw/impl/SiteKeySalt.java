package mpw.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import mpw.api.KeyPurpose;
import mpw.lang.MemoizingSupplier;

public class SiteKeySalt implements mpw.api.SiteKeySalt {
  private final String siteName;
  private final int siteCounter;
  private final KeyPurpose keyPurpose;
  private final Supplier<byte[]> bytes;

  public SiteKeySalt(final String siteName, final int siteCounter, final KeyPurpose keyPurpose) {
    this.siteName = siteName;
    this.siteCounter = siteCounter;
    this.keyPurpose = keyPurpose;
    this.bytes = new MemoizingSupplier<>(() -> generateBytes());
  }

  protected byte[] generateBytes() {
    final var salt = new ByteArrayOutputStream();
    final var writer = new ByteWriter(salt);
    try {
      writer.write(this.keyPurpose.getScope(), StandardCharsets.US_ASCII);
      writer.write(this.siteName, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
      writer.writeInt(this.siteCounter, ByteOrder.BIG_ENDIAN);
    } catch (final IOException ignored) {
    }
    return salt.toByteArray();
  }

  @Override
  public byte[] bytes() {
    return this.bytes.get();
  }

  @Override
  public String toString() {
    return String.format("SiteKeySalt (%s byte(s))", bytes().length);
  }
}
