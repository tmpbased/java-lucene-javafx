package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;

public abstract class A extends A_GeneratedSuperclass {
  abstract void testSwitch(Number value);

  void testCaseInteger(@Case Integer value) {}
}
