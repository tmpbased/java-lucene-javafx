package tilt.lib.receiver.v2;

import java.io.Serializable;
import java.util.List;
import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

public abstract class C<
        X extends List<? extends Number> & Serializable,
        Y extends A<Y>
        >
    extends C_GeneratedSuperclass<X, Y, A<X>> {
  public C() {}

  protected <Q extends X, Z extends C<?, ?> & Serializable> C(Z defaultValue)
      throws Exception {}

  @Override
  abstract void testSwitch(@Switch Number value);

  void testCaseDouble(@Case Double value)
        /** TODO throws java.io.IOException */
      {}
}
