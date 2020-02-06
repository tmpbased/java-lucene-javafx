package tilt.lib.receiver.v2;
/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import tilt.apt.dispatch.annotations.Case;
import tilt.apt.dispatch.annotations.Switch;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
public class NeDispatchBenchmark {
  public static final class Ev0 implements Event {}

  public static final class Ev1 implements Event {}

  public static final class Ev2 implements Event {}

  public static final class Ev3 implements Event {}

  public static final class Ev4 implements Event {}

  public static final class Ev5 implements Event {}

  public static final class Ev6 implements Event {}

  public static final class Ev7 implements Event {}

  public static final int EVENT_COUNT = 8;

  public static final class Out0 extends Node {
    public int[] count;

    public Out0() {
      this.count = new int[EVENT_COUNT];
    }

    @Override
    protected void consume(Flow flow, Event event) {
      if (event instanceof Ev0) {
        this.count[0]++;
      } else if (event instanceof Ev1) {
        this.count[1]++;
      } else if (event instanceof Ev2) {
        this.count[2]++;
      } else if (event instanceof Ev3) {
        this.count[3]++;
      } else if (event instanceof Ev4) {
        this.count[4]++;
      } else if (event instanceof Ev5) {
        this.count[5]++;
      } else if (event instanceof Ev6) {
        this.count[6]++;
      } else if (event instanceof Ev7) {
        this.count[7]++;
      }
    }
  }

  public static final class Out1 extends Node {
    public int[] count;
    private ConcurrentMap<Class<?>, C0> cache;

    private final class C0 extends Consumer {
      private final int index;

      public C0(final int index) {
        this.index = index;
      }

      @Override
      public void consume(Flow flow, Event event) {
        count[this.index]++;
      }
    }

    public Out1() {
      cache = new ConcurrentHashMap<>();
      cache.put(Ev0.class, new C0(0));
      cache.put(Ev1.class, new C0(1));
      cache.put(Ev2.class, new C0(2));
      cache.put(Ev3.class, new C0(3));
      cache.put(Ev4.class, new C0(4));
      cache.put(Ev5.class, new C0(5));
      cache.put(Ev6.class, new C0(6));
      cache.put(Ev7.class, new C0(7));
      count = new int[EVENT_COUNT];
    }

    @Override
    protected void consume(Flow flow, Event event) {
      final C0 consumer = cache.get(event.getClass());
      consumer.consume(flow, event);
    }
  }

  public static final class OutReflect extends NeDispatch {
    public int[] count;

    public OutReflect() {
      super(MethodHandles.lookup());
      count = new int[EVENT_COUNT];
    }

    @Override
    protected void invoke(MethodHandle handle, Flow flow, Event event) throws Throwable {
      handle.invokeExact(this, flow, event);
    }

    public void dispatch(final Flow flow, final Ev0 event) {
      count[0]++;
    }

    public void dispatch(final Flow flow, final Ev1 event) {
      count[1]++;
    }

    public void dispatch(final Flow flow, final Ev2 event) {
      count[2]++;
    }

    public void dispatch(final Flow flow, final Ev3 event) {
      count[3]++;
    }

    public void dispatch(final Flow flow, final Ev4 event) {
      count[4]++;
    }

    public void dispatch(final Flow flow, final Ev5 event) {
      count[5]++;
    }

    public void dispatch(final Flow flow, final Ev6 event) {
      count[6]++;
    }

    public void dispatch(final Flow flow, final Ev7 event) {
      count[7]++;
    }
  }

  public abstract static class OutAnnProc
      extends NeDispatchBenchmark$OutAnnProc_GeneratedSuperclass<Node> {
    public int[] count;

    public OutAnnProc() {
      count = new int[NeDispatchBenchmark.EVENT_COUNT];
    }

    @Override
    protected abstract void consume(Flow flow, @Switch Event event);

    public void dispatch(final Flow flow, @Case final Ev0 event) {
      count[0]++;
    }

    public void dispatch(final Flow flow, @Case final Ev1 event) {
      count[1]++;
    }

    public void dispatch(final Flow flow, @Case final Ev2 event) {
      count[2]++;
    }

    public void dispatch(final Flow flow, @Case final Ev3 event) {
      count[3]++;
    }

    public void dispatch(final Flow flow, @Case final Ev4 event) {
      count[4]++;
    }

    public void dispatch(final Flow flow, @Case final Ev5 event) {
      count[5]++;
    }

    public void dispatch(final Flow flow, @Case final Ev6 event) {
      count[6]++;
    }

    public void dispatch(final Flow flow, @Case final Ev7 event) {
      count[7]++;
    }
  }

  public static final class In extends Node {
    @Override
    protected void consume(Flow flow, Event event) {}

    public void send0(final Event event) {
      send(event);
    }
  }

  public static In in(final Node out) throws Exception {
    final var in = new In();
    final StaticBus g = new StaticBus(Map.of(in, out));
    g.setUp();
    return in;
  }

  private static final Ev6 a = new Ev6();
  private static final Ev7 b = new Ev7();

  private static final Out0 out0 = new Out0();
  private static final Out1 out1 = new Out1();
  private static final OutReflect outReflect = new OutReflect();
  private static final OutAnnProc outAnnProc = OutAnnProc.newInstance();
  private static final In in0, in1, inReflect, inAnnProc;

  static {
    try {
      in0 = in(out0);
      in1 = in(out1);
      inReflect = in(outReflect);
      inAnnProc = in(outAnnProc);
    } catch (final Throwable e) {
      e.printStackTrace();
      throw new AssertionError();
    }
  }

  @Benchmark
  public void testMethod0(final Blackhole bh) throws Throwable {
    in0.send0(a);
    in0.send0(b);
    for (int i = 0; i < EVENT_COUNT; i++) {
      bh.consume(out0.count[i]);
    }
  }

  @Benchmark
  public void testMethod1(final Blackhole bh) throws Throwable {
    in1.send0(a);
    in1.send0(b);
    for (int i = 0; i < EVENT_COUNT; i++) {
      bh.consume(out1.count[i]);
    }
  }

  @Benchmark
  public void testMethodReflect(final Blackhole bh) throws Throwable {
    inReflect.send0(a);
    inReflect.send0(b);
    for (int i = 0; i < EVENT_COUNT; i++) {
      bh.consume(outReflect.count[i]);
    }
  }

  @Benchmark
  public void testMethodAnnProc(final Blackhole bh) throws Throwable {
    inAnnProc.send0(a);
    inAnnProc.send0(b);
    for (int i = 0; i < EVENT_COUNT; i++) {
      bh.consume(outAnnProc.count[i]);
    }
  }
}
