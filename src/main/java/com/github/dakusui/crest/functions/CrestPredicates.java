package com.github.dakusui.crest.functions;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public enum CrestPredicates {
  ;

  public static <T> Predicate<? super T> alwaysTrue() {
    return Formattable.predicate(
        "alwaysTrue",
        t -> true
    );
  }

  public static Predicate<? super Boolean> isTrue() {
    return Formattable.predicate(
        "isTrue",
        (Boolean v) -> v
    );
  }

  public static Predicate<? super Boolean> isFalse() {
    return Formattable.predicate(
        "isFalse",
        (Boolean v) -> !v
    );
  }

  public static <T> Predicate<? super T> isNull() {
    return Formattable.predicate(
        "isNull",
        Objects::isNull
    );
  }

  public static <T> Predicate<? super T> isNotNull() {
    return Formattable.predicate(
        "isNotNull",
        Objects::nonNull
    );
  }

  public static <T> Predicate<? super T> equalTo(T value) {
    return Formattable.predicate(
        String.format("equalTo[%s]", value),
        v -> Objects.equals(v, value)
    );
  }

  public static <T> Predicate<? super T> isSameAs(T value) {
    return Formattable.predicate(
        String.format("==[%s]", value),
        v -> v == value
    );
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<? super T> isInstanceOf(Class<?> value) {
    requireNonNull(value);
    //noinspection SimplifiableConditionalExpression
    return Formattable.predicate(
        String.format("isInstanceOf[%s]", value.getCanonicalName()),
        v -> v == null ?
            false :
            value.isAssignableFrom(v.getClass())
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
        String.format("=[%s]", value),
        v -> v.compareTo(value) == 0
    );
  }

  public static Predicate<? super String> matchesRegex(String regex) {
    requireNonNull(regex);
    return Formattable.predicate(
        String.format("matchesRegex[%s]", requireNonNull(regex)),
        s -> s.matches(regex)
    );
  }

  public static Predicate<? super String> containsString(String string) {
    return Formattable.predicate(
        String.format("containsString[%s]", requireNonNull(string)),
        s -> s.contains(string)
    );
  }

  public static Predicate<? super String> startsWith(String string) {
    return Formattable.predicate(
        String.format("containsString[%s]", requireNonNull(string)),
        s -> s.startsWith(string)
    );
  }

  public static Predicate<? super String> endsWith(String string) {
    return Formattable.predicate(
        String.format("endsWith[%s]", requireNonNull(string)),
        s -> s.endsWith(string)
    );
  }

  public static Predicate<? super String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return Formattable.predicate(
        String.format("equalsIgnoreCase[%s]", requireNonNull(string)),
        s -> s.equalsIgnoreCase(string)
    );
  }

  public static Predicate<? super String> isEmptyString() {
    return Formattable.predicate(
        "isEmpty",
        String::isEmpty
    );
  }

  public static Predicate<? super String> isEmptyOrNullString() {
    return Formattable.predicate(
        "isEmptyOrNull",
        s -> Objects.isNull(s) || isEmptyString().test(s)
    );
  }

  public static <E> Predicate<? super Collection<? super E>> containsAll(Collection<? extends E> collection) {
    requireNonNull(collection);
    return Formattable.predicate(
        String.format("containsAll%s", collection),
        c -> c.containsAll(collection)
    );
  }

  /*
   * in any order
   * unlike AssertJ, this method returns true even if target collection does not over all the items in given
   * collection as long as all the items in the target collection are found in given one.
   */
  public static <E> Predicate<? super Collection<? super E>> containsOnly(Collection<? extends E> collection) {
    requireNonNull(collection);
    return Formattable.predicate(
        String.format("containsOnly%s", collection),
        collection::containsAll
    );
  }

  /*
   * This is more similar to AssertJ's containsOnly method than our containsOnly.
   * This method returns true if and only if all the items in the target collection
   * and the given collection are equal.
   */
  public static <E> Predicate<? super Collection<? super E>> containsExactly(Collection<? extends E> collection) {
    requireNonNull(collection);
    return Formattable.predicate(
        String.format("containsExactly%s", collection),
        c -> c.containsAll(collection) && collection.containsAll(c)
    );
  }

  public static <E> Predicate<? super Collection<? super E>> contains(E entry) {
    requireNonNull(entry);
    return Formattable.predicate(
        String.format("contains[%s]", entry),
        c -> c.contains(entry)
    );
  }

  public static Predicate<? super Collection> isEmpty() {
    return Formattable.predicate(
        "isEmpty",
        Collection::isEmpty
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> allMatch(Predicate<E> predicate) {

    return Formattable.predicate(
        String.format("allMatch[%s]", requireNonNull(predicate)),
        stream -> stream.allMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> noneMatch(Predicate<E> predicate) {
    return Formattable.predicate(
        String.format("noneMatch[%s]", requireNonNull(predicate)),
        stream -> stream.noneMatch(predicate)
    );
  }

  public static <E> Predicate<? super Stream<? extends E>> anyMatch(Predicate<E> predicate) {
    return Formattable.predicate(
        String.format("anyMatch[%s]", requireNonNull(predicate)),
        stream -> stream.anyMatch(predicate)
    );
  }
}
