package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;

public final class B extends A<Number> {
  @Override
  final void testSwitch(Number value) {
    // XD
  }

  void testCaseDouble(@Case Double value) {
  }
}
