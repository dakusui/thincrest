package com.github.dakusui.crest;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public enum CrestFunctions {
  ;

  public static <E> Function<List<E>, E> elementAt(int i) {
    return Formattable.function(
        String.format("elementAt[%s]", i),
        es -> es.get(i)
    );
  }

  public static <E> Function<? super Collection<E>, Integer> size() {
    return Formattable.function(
        "size",
        Collection::size
    );
  }

  public static <E> Function<? super Collection<E>, Stream<? extends E>> stream() {
    return Formattable.function(
        "stream",
        Collection::stream
    );
  }
}
