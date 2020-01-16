package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public abstract class A<T> implements Comparable<T> {
  // TODO generate nothing if there is no Switch annotation.
  abstract void testSwitch(@Switch Number value);

  void testCaseInteger(@Case Integer value) {}

  @Override
  public int compareTo(T o) {
    return 0;
  }
}
