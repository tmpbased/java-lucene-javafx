package mpw.impl;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import mpw.api.Renderer;
import mpw.template.Template;

final class SiteKey extends Bytes implements mpw.api.SiteKey {
  public SiteKey(final byte[] bytes) {
    super(bytes);
  }

  public String format() {
    // cvccvcvcv
    // c = bcdfghjklmnpqrstvwxyz
    // v = aeiou
    Map<Character, String> map = new HashMap<>();
    map.put('c', "bcdfghjklmnpqrstvwxyz");
    map.put('v', "aeiou");
    final String fmt = "cvccvcvcv";
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < fmt.length(); i++) {
      final String chars = map.get(fmt.charAt(i));
      sb.append(chars.charAt(Byte.toUnsignedInt(bytes()[i + 1]) % chars.length()));
    }
    return sb.toString();
  }

  @Override
  public String format(final Template template) {
    final ByteBuffer buffer = ByteBuffer.wrap(bytes()).asReadOnlyBuffer();
    try {
      return template.render(buffer);
    } catch (final BufferUnderflowException e) {
      throw new IllegalStateException(String.format("%s is too short for the %s", this, template));
    }
  }

  /**
   * Move the start of the buffer to the current position, while putting all bytes that were
   * preceding the current position at the end of the buffer (or just before its limit).
   *
   * The position is set to zero and the mark is discarded.
   */
  private static void rotateLeft(final ByteBuffer buffer) {
    final int shift = buffer.position();
    final ByteBuffer newBuffer = ByteBuffer.allocate(shift + buffer.remaining());
    newBuffer.put(buffer.slice()).put(buffer.rewind().slice().limit(shift)).flip();
    buffer.put(newBuffer).rewind();
  }

  @Override
  public String format(final Renderer<? extends Template> templateGen) {
    final ByteBuffer buffer = ByteBuffer.wrap(bytes());
    final Template template;
    template = templateGen.render(buffer.asReadOnlyBuffer());
    rotateLeft(buffer); // TODO ???
    try {
      return template.render(buffer.slice().asReadOnlyBuffer());
    } catch (final BufferUnderflowException e) {
      throw new IllegalStateException(String.format("%s is too short for the %s", this, template));
    }
  }

  @Override
  public String toString() {
    return String.format("SiteKey (%s byte(s))", bytes().length);
  }
}
