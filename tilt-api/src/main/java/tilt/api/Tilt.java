package tilt.api;

import java.io.Closeable;

import tilt.lib.receiver.Receiver;

public interface Tilt extends Receiver, Closeable {
  @Override
  void close();
}
