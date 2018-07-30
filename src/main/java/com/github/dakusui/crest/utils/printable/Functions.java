package com.github.dakusui.crest.utils.printable;

import com.github.dakusui.crest.core.InternalUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public enum Functions {
  ;

  public static <E> Function<E, E> identity() {
    return Printable.function(
        "identity",
        Function.identity()
    );
  }

  public static <E> Function<? super E, String> stringify() {
    return Printable.function(
        "toString",
        Object::toString
    );
  }

  public static Function<? super String, Integer> length() {
    return Printable.function(
        "length",
        String::length
    );
  }

  public static <E> Function<List<? extends E>, ? extends E> elementAt(int i) {
    return Printable.function(
        () -> String.format("elementAt[%s]", i),
        es -> (E) es.get(i)
    );
  }

  public static Function<? super Collection, Integer> size() {
    return Printable.function(
        "size",
        Collection::size
    );
  }

  public static <E> Function<Collection<? extends E>, Stream<? extends E>> stream() {
    return Printable.function(
        "stream",
        Collection::stream
    );
  }

  public static <E> Function<? super Object, ? extends E> cast(Class<E> type) {
    return Printable.function(
        () -> String.format("castTo[%s]", requireNonNull(type).getSimpleName()),
        type::cast
    );
  }

  public static <I extends Collection<? extends E>, E> Function<I, List<E>> collectionToList() {
    return Printable.function("collectionToList", (I c) -> new LinkedList<E>() {
      {
        addAll(c);
      }
    });
  }

  public static <E> Function<E[], List<E>> arrayToList() {
    return Printable.function("arrayToList", Arrays::asList);
  }

  public static Function<String, Integer> countLines() {
    return Printable.function("countLines", (String s) -> s.split("\n").length);
  }

  @SuppressWarnings("unchecked")
  public static <I, E> Function<? super I, ? extends E> invoke(String methodName, Object... args) {
    return Printable.function(
        () -> String.format("@%s%s", methodName, asList(args)),
        (I target) -> (E) InternalUtils.invokeMethod(target, methodName, args)
    );
  }
}
