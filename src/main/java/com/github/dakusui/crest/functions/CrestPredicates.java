package com.github.dakusui.crest.functions;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public enum CrestPredicates {
  ;

  public static <T> Predicate<? super T> alwaysTrue() {
    return Formattable.predicate(
        "alwaysTrue",
        t -> true
    );
  }

  public static <T> Predicate<? super T> equalTo(T value) {
    return Formattable.predicate(
        String.format("equalTo[%s]", value),
        v -> Objects.equals(v, value)
    );
  }

  public static <T> Predicate<? super T> invoke(String methodName, Object[] args) {
    return Formattable.predicate(
        String.format("@%s%s", methodName, asList(args)),
        (Object target) -> (boolean) InternalUtils.invokeMethod(target, methodName, args)
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> gt(T value) {
    return Formattable.predicate(
        String.format(">[%s]", value),
        v -> v.compareTo(value) > 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> ge(T value) {
    return Formattable.predicate(
        String.format(">=[%s]", value),
        v -> v.compareTo(value) >= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> lt(T value) {
    return Formattable.predicate(
        String.format("<[%s]", value),
        v -> v.compareTo(value) < 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> le(T value) {
    return Formattable.predicate(
        String.format("<=[%s]", value),
        v -> v.compareTo(value) <= 0
    );
  }

  public static <T extends Comparable<? super T>> Predicate<? super T> eq(T value) {
    return Formattable.predicate(
        String.format("==[%s]", value),
        v -> v.compareTo(value) == 0
    );
  }

  public static Predicate<? super String> matchesRegex(String regex) {
    Objects.requireNonNull(regex);
    return Formattable.predicate(
        String.format("matchesRegex[%s]", regex),
        s -> s.matches(regex)
    );
  }

  public static Predicate<? super String> containsString(String string) {
    Objects.requireNonNull(string);
    return Formattable.predicate(
        String.format("containsString[%s]", string),
        s -> s.contains(string)
    );
  }

  public static <E> Predicate<? super Collection<? super E>> containsAll(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    return Formattable.predicate(
        String.format("containsAll%s", collection),
        c -> c.containsAll(collection)
    );
  }

  public static <E> Predicate<? super Collection<? super E>> contains(E entry) {
    Objects.requireNonNull(entry);
    return Formattable.predicate(
        String.format("contains[%s]", entry),
        c -> c.contains(entry)
    );
  }

  public static <E> Predicate<? super Collection<? super E>> isEmpty() {
    return Formattable.predicate(
        "isEmpty",
        Collection::isEmpty
    );
  }


  public static <E> Predicate<? super Stream<? extends E>> allMatch(Predicate<E> predicate) {
    Objects.requireNonNull(predicate);
    return Formattable.predicate(
        String.format("allMatch[%s]", predicate),
        stream -> stream.allMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    Objects.requireNonNull(predicate);
    return Formattable.predicate(
        String.format("noneMatch[%s]", predicate),
        stream -> stream.noneMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    Objects.requireNonNull(predicate);
    return Formattable.predicate(
        String.format("anyMatch[%s]", predicate),
        stream -> stream.anyMatch(predicate)
    );
  }
}
