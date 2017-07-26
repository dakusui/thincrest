package com.github.dakusui.crest.functions;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

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
        (Object target) -> (E) InternalUtils.invokeMethod(target, methodName, args)
    );
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
