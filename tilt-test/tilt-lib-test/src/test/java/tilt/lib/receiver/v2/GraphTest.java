package tilt.lib.receiver.v2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class GraphTest {
  final class Ping implements Event {
  };

  final class Pong implements Event {
  };

  private final Event ping = new Ping(), pong = new Pong();

  private abstract class A extends Node {
    @Override
    public abstract void consume(final Flow flow, final Event event);

    public void ping() {
      send(ping);
    }

    public void pong() {
      send(pong);
    }
  }

  private abstract class B extends Node {
    @Override
    public abstract void consume(final Flow flow, final Event event);

    public void pong() {
      send(pong);
    }
  }

  private abstract class Interceptor extends Node {
    @Override
    public void consume(final Flow flow, final Event event) {
      flow.send(event);
    }
  }

  @Test
  public void otherGraph() throws Exception {
    boolean[] ok = { false };
    final class Fa extends A {
      @Override
      public void consume(Flow ctx, Event event) {
        if (event == pong) {
          ok[0] = true;
        } else {
          fail();
        }
      }
    }
    final var a = new Fa();
    final class Fb extends B {
      @Override
      public void consume(Flow flow, Event event) {
        pong();
      }
    }
    final var b = new Fb();
    final StaticBus g1 = new StaticBus(Map.of(a, new ByClass(Map.of(ping.getClass(), b)))),
        g2 = new StaticBus(Map.of(b, new ByClass(Map.of(pong.getClass(), a))));
    g1.setUp();
    g2.setUp();
    a.ping();
    assertArrayEquals(new boolean[] { true }, ok);
  }

  @Test
  public void directReqResp() throws Exception {
    boolean[] ok = { false };
    final class Fa extends A {
      @Override
      public void consume(final Flow ctx, final Event event) {
        if (event == pong) {
          ok[0] = true;
        } else {
          fail();
        }
      }
    }
    final var a = new Fa();
    final class Fb extends B {
      @Override
      public void consume(Flow flow, Event event) {
        pong();
      }
    }
    final var b = new Fb();
    final StaticBus g = new StaticBus(Map.of( // a -> b
        a, new ByClass(Map.of(ping.getClass(), b)), // b -> a
        b, new ByClass(Map.of(pong.getClass(), a))));
    g.setUp();
    a.ping();
    assertArrayEquals(new boolean[] { true }, ok);
  }

  @Test
  public void unknownMessage() throws Exception {
    final class Fa extends A {
      @Override
      public void consume(final Flow ctx, final Event event) {
        if (event == pong) {
          send(event);
        }
      }
    }
    final var a = new Fa();
    final class Fb extends B {
      @Override
      public void consume(final Flow ctx, final Event event) {
        fail();
      }
    }
    final var b = new Fb();
    final StaticBus g = new StaticBus(Map.of( // a -> b
        a, new ByClass(Map.of(ping.getClass(), b)), // b -> a
        b, new ByClass(Map.of(pong.getClass(), a))));
    g.setUp();
    try {
      a.pong();
      fail();
    } catch (final BusException e) {
    }
  }

  @Test
  public void bidirectionalInterceptor() throws Exception {
    boolean[] ok = { false, false, false };
    final class Fa extends A {
      @Override
      public void consume(final Flow ctx, final Event event) {
        if (event == pong) {
          ok[2] = true;
        } else {
          fail();
        }
      }
    }
    final var a = new Fa();
    final class Fb extends B {
      @Override
      public void consume(Flow flow, Event event) {
        pong();
      }
    }
    final var b = new Fb();
    final class FInterceptor extends Interceptor {
      @Override
      public void consume(final Flow ctx, final Event event) {
        if (event == ping) {
          ok[0] = true;
        } else if (event == pong) {
          ok[1] = true;
        } else {
          fail();
        }
        super.consume(ctx, event);
      }
    }
    final var c = new FInterceptor();
    final StaticBus g = new StaticBus(Map.of( // a -> b
        a, new ByClass(Map.of(ping.getClass(), new CrSeq(List.of(c, b)))), // b -> a
        b, new ByClass(Map.of(pong.getClass(), new CrSeq(List.of(c, a))))));
    g.setUp();
    a.ping();
    assertArrayEquals(new boolean[] { true, true, true }, ok);
  }

  @Test
  public void multipleReceivers() throws Exception {
    int[] responses = { 0 };
    final class Fa extends A {
      @Override
      public void consume(final Flow ctx, final Event event) {
        if (event == pong) {
          responses[0]++;
        } else {
          fail();
        }
      }
    }
    final var a = new Fa();
    final class Fb extends B {
      @Override
      public void consume(Flow flow, Event event) {
        pong();
      }
    }
    final var b1 = new Fb();
    final var b2 = new Fb();
    final StaticBus g = new StaticBus(Map.of( // a -> b1, b2
        a, new ByClass(Map.of(ping.getClass(), new CrPar(List.of(b1, b2)))), // b1 -> a
        b1, new ByClass(Map.of(pong.getClass(), a)), // b2 -> a
        b2, new ByClass(Map.of(pong.getClass(), a))));
    g.setUp();
    a.ping();
    assertEquals(2, responses[0]);
  }

  @Test
  public void summator() throws Exception {
    final class Num implements Event {
      final int num;

      public Num(final int num) {
        this.num = num;
      }
    }
    final class In extends Node {
      @Override
      protected void consume(Flow flow, Event event) {
      }
    }
    final class Pow extends Node {
      private final int pow;

      public Pow(final int pow) {
        this.pow = pow;
      }

      @Override
      protected void consume(Flow flow, Event event) {
        flow.send(new Num((int) Math.pow(Num.class.cast(event).num, pow)));
      }
    }
    final class Sum extends Node {
      private int sum;

      @Override
      protected void consume(Flow flow, Event event) {
        sum += Num.class.cast(event).num;
        flow.send(new Num(sum));
      }
    }
    final class Out extends Node {
      final List<Integer> values = new ArrayList<>();

      @Override
      protected void consume(Flow flow, Event event) {
        this.values.add(Num.class.cast(event).num);
      }
    }
    final var in = new In();
    final var out = new Out();
    final StaticBus g = new StaticBus(
        Map.of(in, new CrSeq(List.of(new CrPar(List.of(new Pow(2), new Pow(3))), new Sum(), out))));
    g.setUp();

    in.send(new Num(2));

    assertEquals(4 + 8, out.values.get(out.values.size() - 1));
  }

  @Test
  public void seqSeq() throws Exception {
    final class Num implements Event {
      final int num;

      public Num(final int num) {
        this.num = num;
      }
    }
    final class In extends Node {
      @Override
      protected void consume(Flow flow, Event event) {
      }
    }
    final class Pow extends Node {
      private final int pow;

      public Pow(final int pow) {
        this.pow = pow;
      }

      @Override
      protected void consume(Flow flow, Event event) {
        flow.send(new Num((int) Math.pow(Num.class.cast(event).num, pow)));
      }
    }
    final class Sum extends Node {
      private int sum;

      @Override
      protected void consume(Flow flow, Event event) {
        sum += Num.class.cast(event).num;
        flow.send(new Num(sum));
      }
    }
    final class Out extends Node {
      final List<Integer> values = new ArrayList<>();

      @Override
      protected void consume(Flow flow, Event event) {
        this.values.add(Num.class.cast(event).num);
      }
    }
    final var in = new In();
    final var out = new Out();
    final StaticBus g = new StaticBus(
        Map.of(in, new CrSeq(List.of(new CrSeq(List.of(new Pow(2), new Sum())), out))));
    g.setUp();

    in.send(new Num(2));
    in.send(new Num(3));

    assertEquals(4 + 9, out.values.get(out.values.size() - 1));
  }

  @Test
  public void dispatch() throws Exception {
    final class In extends Node {
      @Override
      protected void consume(Flow flow, Event event) {
      }
    }
    final class EvA implements Event {
    }
    final class EvB implements Event {
    }
    @SuppressWarnings("unused")
    final class Out extends NeDispatch {
      int aCount, bCount;

      public Out() {
        super(MethodHandles.lookup());
      }

      @Override
      protected void invoke(MethodHandle handle, Flow flow, Event event) throws Throwable {
        handle.invokeExact(this, flow, event);
      }

      void dispatch(final Flow flow, final EvA event) {
        aCount++;
      }

      void dispatch(final Flow flow, final EvB event) {
        bCount++;
      }

      void reset() {
        this.aCount = this.bCount = 0;
      }

      void assertCount(final int aCount, final int bCount) {
        assertEquals(aCount, this.aCount);
        assertEquals(bCount, this.bCount);
      }
    }
    final var in = new In();
    final var out = new Out();
    final StaticBus g = new StaticBus(Map.of(in, out));
    g.setUp();

    in.send(new EvA());
    out.assertCount(1, 0);
    out.reset();

    in.send(new EvB());
    out.assertCount(0, 1);
    out.reset();
  }
}
