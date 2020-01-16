package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public final class B extends A<Number> {
  @Override
  final void testSwitch(@Switch Number value) {
    // XD
  }

  void testCaseDouble(@Case Double value) {
  }
}
