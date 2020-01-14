package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;

public abstract class A {
  abstract void testSwitch(Number value);

  void testCaseInteger(@Case Integer value) {

  }

  public A newInstance() {
    return new GeneratedSubclass.A();
  }
}
