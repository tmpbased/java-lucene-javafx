package mpw.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import mpw.api.KeyPurpose;
import mpw.lang.MemoizingSupplier;

public final class MasterKeySalt implements mpw.api.MasterKeySalt {
  private final String fullName;
  private final Supplier<byte[]> bytes;

  public MasterKeySalt(final String fullName) {
    this.fullName = fullName;
    this.bytes = new MemoizingSupplier<>(this::generateBytes);
  }

  protected byte[] generateBytes() {
    final var salt = new ByteArrayOutputStream();
    final var writer = new ByteWriter(salt);
    try {
      writer.write(KeyPurpose.Authentication.getScope(), StandardCharsets.US_ASCII);
      writer.write(this.fullName, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
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
    return String.format("MasterKeySalt (%s byte(s))", bytes().length);
  }
}
