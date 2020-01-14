package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;

public abstract class C extends A {
  void testCaseDouble(@Case Double value) {
  }

  public C newInstance() {
    return new GeneratedSubclass.C();
  }
}
