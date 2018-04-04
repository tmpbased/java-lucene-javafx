package mpw.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

class ByteWriter {
  private final OutputStream os;

  public ByteWriter(final OutputStream os) {
    this.os = os;
  }

  protected void writeInt(final int value, final ByteOrder order) throws IOException {
    final byte[] bytes = ByteBuffer.allocate(4).order(order).putInt(value).array();
    this.os.write(bytes);
  }

  protected void writeLong(final long value, final ByteOrder order) throws IOException {
    final byte[] bytes = ByteBuffer.allocate(8).order(order).putLong(value).array();
    this.os.write(bytes);
  }

  protected void write(final String str, final Charset charset) throws IOException {
    final byte[] bytes = str.getBytes(charset);
    this.os.write(bytes);
  }

  protected void write(final String str, final Charset charset, final ByteOrder order)
      throws IOException {
    final byte[] bytes = str.getBytes(charset);
    writeInt(bytes.length, order);
    this.os.write(bytes);
  }
}
