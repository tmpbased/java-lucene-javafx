package tilt.lib.receiver.v2;

import java.io.IOException;

import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public abstract class A<T> implements Comparable<T> {
  abstract void testSwitch(@Switch Number value) throws IOException;

  void testCaseInteger(@Case Integer value) {}

  @Override
  public int compareTo(T o) {
    return 0;
  }
}
