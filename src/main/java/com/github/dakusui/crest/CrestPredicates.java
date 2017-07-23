package com.github.dakusui.crest;

import java.util.Objects;
import java.util.function.Predicate;

public enum CrestPredicates {
  ;

  public static <T> Predicate<T> equalsTo(T value) {
    return Formattable.predicate(
        String.format("equalTo'%s'", value),
        v -> Objects.equals(v, value)
    );
  }
}
