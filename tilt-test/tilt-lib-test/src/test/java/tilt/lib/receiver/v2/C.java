package tilt.lib.receiver.v2;

import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public abstract class C<X> extends C_GeneratedSuperclass<X, A<X>> {
  public C() {}

  public C(Number value) throws Exception { // TODO newInstance(value)
  }

  @Override
  abstract void testSwitch(@Switch Number value);

  void testCaseDouble(@Case Double value)
        /** TODO throws java.io.IOException */
      {}
}
