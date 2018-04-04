package tilt.lib.receiver.v2;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public abstract class NeDispatch extends Node {
  private final Lookup lookup;
  private final ConcurrentMap<Class<?>, BiConsumer<Flow, Event>> cache;

  protected NeDispatch(final Lookup lookup) {
    this.lookup = lookup;
    this.cache = new ConcurrentHashMap<>();
  }

  @Override
  protected void setUp(Flow flow) {
    super.setUp(flow);
    fillCache();
  }

  private void fillCache() {
    Class<?> clazz = getClass();
    while (clazz != NeDispatch.class) {
      for (final Method method : clazz.getDeclaredMethods()) {
        final String methodName = method.getName();
        if (methodName.equals("dispatch") == false) {
          continue;
        }
        if (method.getParameterCount() != 2) {
          throw new BusException(String.format("Method %s has unexpected signature %s", methodName, method));
        }
        final Class<?>[] parameterClasses = method.getParameterTypes();
        final Class<?> flowClass = parameterClasses[0];
        final Class<?> eventClass = parameterClasses[1];
        if (flowClass != Flow.class || Event.class.isAssignableFrom(eventClass) == false) {
          throw new BusException(String.format("Method %s has unexpected signature %s", methodName, method));
        }
        if ((eventClass.getModifiers() & Modifier.FINAL) == 0) {
          throw new BusException(
              String.format("Unexpectedly inheritable event %s, received at %s", eventClass, getClass()));
        }
        try {
          MethodHandle handle = unreflect(method, methodName, eventClass);
          this.cache.put(eventClass, (flow, event) -> tryInvoke(handle, flow, event));
        } catch (final IllegalAccessException e) {
          throw new BusException(
              String.format("Method %s for %s is not accessible in %s", methodName, eventClass, getClass()), e);
        }
      }
      clazz = clazz.getSuperclass();
    }
  }

  private MethodHandle unreflect(final Method method, final String methodName, final Class<?> eventClass) throws IllegalAccessException {
    final MethodHandle handle = this.lookup.unreflect(method);
    return handle.asType(handle.type().changeParameterType(2, Event.class)); // XXX for invokeExact
  }

  @Override
  protected final void consume(Flow flow, Event event) {
    final BiConsumer<Flow, Event> consumer = this.cache.get(event.getClass());
    if (consumer == null) {
      throw new BusException(String.format("No handler for event %s in %s", event.getClass(), getClass()));
    }
    consumer.accept(flow, event);
  }

  private final void tryInvoke(final MethodHandle handle, final Flow flow, final Event event) {
    try {
      invoke(handle, flow, event);
    } catch (final Throwable e) {
      throw new BusException(String.format("Can't invoke method for %s at %s", event.getClass(), getClass()), e);
    }
  }

  /** allow implementors to call invokeExact */
  protected void invoke(final MethodHandle handle, final Flow flow, final Event event) throws Throwable {
    handle.invoke(this, flow, event);
  }
}
