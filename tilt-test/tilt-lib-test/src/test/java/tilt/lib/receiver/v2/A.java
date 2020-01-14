package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;

public abstract class A extends GeneratedSuperclass.A {
  abstract void testSwitch(Number value);

  void testCaseInteger(@Case Integer value) {}
}
