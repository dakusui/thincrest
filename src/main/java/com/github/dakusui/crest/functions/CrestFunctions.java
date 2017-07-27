package com.github.dakusui.crest.functions;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

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
  public static <I, E> Function<? super I, ? extends E> invoke(String methodName, Object... args) {
    return Formattable.function(
        String.format("@%s%s", methodName, asList(args)),
        (I target) -> (E) InternalUtils.invokeMethod(target, methodName, args)
    );
  }

  public static Function<? super String, Integer> length() {
    return Formattable.function(
        "length",
        String::length
    );
  }

  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Formattable.function(
        String.format("elementAt[%s]", i),
        es -> (E) es.get(i)
    );
  }

  public static Function<? super Collection, Integer> size() {
    return Formattable.function(
        "size",
        Collection::size
    );
  }

  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Formattable.function(
        "stream",
        Collection::stream
    );
  }

  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Formattable.function(
        String.format("castTo[%s]", requireNonNull(type).getSimpleName()),
        type::cast
    );
  }

  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Formattable.function("collectionToList", (I c) -> new LinkedList<E>() {
      {
        addAll(c);
      }
    });
  }
}
