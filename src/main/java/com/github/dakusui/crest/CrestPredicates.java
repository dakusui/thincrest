package com.github.dakusui.crest;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public enum CrestPredicates {
  ;
  public static <T> Predicate<? super T> equalTo(T value) {
    return Formattable.predicate(
        String.format("equalTo[%s]", value),
        v -> Objects.equals(v, value)
    );
  }

  public static <T extends Comparable<? super T>> Predicate<T> gt(T value) {
    return Formattable.predicate(
        String.format(">[%s]", value),
        v -> v.compareTo(value) > 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return Formattable.predicate(
        String.format(">=[%s]", value),
        v -> v.compareTo(value) >= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return Formattable.predicate(
        String.format("<[%s]", value),
        v -> v.compareTo(value) < 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return Formattable.predicate(
        String.format("<=[%s]", value),
        v -> v.compareTo(value) <= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<T> eq(T value) {
    return Formattable.predicate(
        String.format("==[%s]", value),
        v -> v.compareTo(value) == 0
    );
  }

  public static <E> Predicate<Stream<? extends E>> allMatch(Predicate<E> predicate) {
    return Formattable.predicate(
        String.format("allMatch[%s]", predicate),
        stream -> stream.allMatch(predicate)
    );
  }

  public static <E> Predicate<Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return Formattable.predicate(
        String.format("noneMatch[%s]", predicate),
        stream -> stream.noneMatch(predicate)
    );
  }

  public static <E> Predicate<Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return Formattable.predicate(
        String.format("anyMatch[%s]", predicate),
        stream -> stream.anyMatch(predicate)
    );
  }
}
