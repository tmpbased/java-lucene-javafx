package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public abstract class C<X/** TODO extends A<X> */> extends C_GeneratedSuperclass<X, A<X>> {
  public C() {
  }

  protected C(Number defaultValue) throws Exception {
  }

  @Override
  abstract void testSwitch(@Switch Number value);

  void testCaseDouble(@Case Double value) /** TODO throws java.io.IOException */ {
  }
}
