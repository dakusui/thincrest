package com.github.dakusui.crest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum CrestFunctions {
  ;

  public static <E> Function<E, E> identity() {
    return Formattable.function(
        "identity",
        Function.identity()
    );
  }

  public static <E> Function<? super E, String> stringify() {
    return Formattable.function(
        "toString",
        Object::toString
    );
  }

  @SuppressWarnings("unchecked")
  public static <E> Function<? super Object, ? extends E> invoke(String methodName, Object... args) {
    return Formattable.function(
        String.format("invoke[%s,%s]", methodName, asList(args)),
        (Object target) -> (E) invokeMethod(target, methodName, args)
    );
  }

  @SuppressWarnings("unchecked")
  private static <R> R invokeMethod(Object target, String methodName, Object[] args) {
    try {
      return (R) findMethod(Objects.requireNonNull(target).getClass(), methodName, args).invoke(target, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static Method findMethod(Class<?> aClass, String methodName, Object[] args) {
    return getIfOnlyOneElseThrow(
        Arrays.stream(
            aClass.getMethods()
        ).filter(
            (Method m) -> m.getName().equals(methodName)
        ).filter(
            (Method m) -> areCompatible(m.getParameterTypes(), args)
        ).collect(
            toList()
        )
    ).orElseThrow(
        () -> new RuntimeException(String.format(
            "Method matching '%s%s' was not found or more than one were mathing in %s.",
            methodName,
            asList(args),
            aClass.getCanonicalName()
        ))
    );
  }

  @SuppressWarnings("unchecked")
  private static boolean areCompatible(Class[] formalParameters, Object[] args) {
    if (formalParameters.length != args.length)
      return false;
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null)
        continue;
      if (!formalParameters[i].isAssignableFrom(args[i].getClass()))
        return false;
    }
    return true;
  }

  private static <T> Optional<T> getIfOnlyOneElseThrow(List<T> in) {
    if (in.size() == 1)
      return Optional.of(in.get(0));
    return Optional.empty();
  }

  public static <E> Function<? super List<? extends E>, E> elementAt(int i) {
    return Formattable.function(
        String.format("elementAt[%s]", i),
        es -> es.get(i)
    );
  }

  public static <E> Function<? super Collection<? extends E>, Integer> size() {
    return Formattable.function(
        "size",
        Collection::size
    );
  }

  public static <E> Function<? super Collection<? extends E>, Stream<? extends E>> stream() {
    return Formattable.function(
        "stream",
        Collection::stream
    );
  }
}
