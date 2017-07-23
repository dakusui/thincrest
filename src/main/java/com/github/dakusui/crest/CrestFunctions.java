package com.github.dakusui.crest;

import java.util.List;
import java.util.function.Function;

public enum CrestFunctions {
  ;

  public static <E> Function<List<E>, E> elementAt(int i) {
    return Formattable.function(
        String.format("elementAt%s", i),
        es -> es.get(i)
    );
  }
}
