package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.core.Printable;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.matcherbuilders.primitives.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * A facade class of 'thincrest'.
 */
public enum Crest {
  ;

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return Matcher.Conjunctive.create(true, asList(matchers));
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return Matcher.Disjunctive.create(true, asList(matchers));
  }

  public static <I, S extends AsObject<I, I, S>> AsObject<I, I, S> asObject() {
    return new AsObject<>(CrestFunctions.identity());
  }

  public static <I, O, S extends AsObject<I, O, S>> AsObject<I, O, S> asObject(String methodName, Object... args) {
    return new AsObject<>(CrestFunctions.<I, O>invoke(methodName, args));
  }

  public static <I, O, S extends AsObject<I, O, S>> AsObject<I, O, S> asObject(Function<? super I, ? extends O> function) {
    return new AsObject<>(function);
  }

  public static AsBoolean<Boolean> asBoolean() {
    return asBoolean(CrestFunctions.identity());
  }

  public static <I> AsBoolean<I> asBoolean(String methodName, Object... args) {
    return asBoolean(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Boolean.class)));
  }

  public static <I> AsBoolean<I> asBoolean(Predicate<? super I> predicate) {
    requireNonNull(predicate);
    return asBoolean(Printable.function(predicate.toString(), predicate::test));
  }

  public static <I> AsBoolean<I> asBoolean(Function<? super I, Boolean> function) {
    return new AsBoolean<>(function);
  }

  public static <I> AsByte<I> asByte(Function<? super I, Byte> function) {
    return new AsByte<>(function);
  }

  public static <I> AsByte<I> asByte(String methodName, Object... args) {
    return asByte(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Byte.class)));
  }

  public static AsByte<Byte> asByte() {
    return asByte(CrestFunctions.identity());
  }

  public static <I> AsChar<I> asChar(Function<? super I, Character> function) {
    return new AsChar<>(function);
  }

  public static <I> AsChar<I> asChar(String methodName, Object... args) {
    return asChar(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Character.class)));
  }

  public static AsChar<Character> asChar() {
    return asChar(CrestFunctions.identity());
  }

  public static <I> AsShort<I> asShort(Function<? super I, Short> function) {
    return new AsShort<>(function);
  }

  public static <I> AsShort<I> asShort(String methodName, Object... args) {
    return asShort(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Short.class)));
  }

  public static AsShort<Short> asShort() {
    return asShort(CrestFunctions.identity());
  }

  public static <I> AsInteger<I> asInteger(Function<? super I, Integer> function) {
    return new AsInteger<>(function);
  }

  public static <I> AsInteger<I> asInteger(String methodName, Object... args) {
    return asInteger(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Integer.class)));
  }

  public static AsInteger<Integer> asInteger() {
    return asInteger(CrestFunctions.identity());
  }

  public static <I> AsLong<I> asLong(Function<? super I, Long> function) {
    return new AsLong<>(function);
  }

  public static <I> AsLong<I> asLong(String methodName, Object... args) {
    return asLong(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Long.class)));
  }

  public static AsLong<Long> asLong() {
    return asLong(CrestFunctions.identity());
  }

  public static <I> AsFloat<I> asFloat(Function<? super I, Float> function) {
    return new AsFloat<>(function);
  }

  public static <I> AsFloat<I> asFloat(String methodName, Object... args) {
    return asFloat(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Float.class)));
  }

  public static AsFloat<Float> asFloat() {
    return asFloat(CrestFunctions.identity());
  }

  public static <I> AsDouble<I> asDouble(Function<? super I, Double> function) {
    return new AsDouble<>(function);
  }

  public static <I> AsDouble<I> asDouble(String methodName, Object... args) {
    return asDouble(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Double.class)));
  }

  public static AsDouble<Double> asDouble() {
    return asDouble(CrestFunctions.identity());
  }

  /*
   * Casts a given object into the given comparable type
   */
  public static <I extends Comparable<? super I>, S extends AsComparable<I, I, S>>
  S asComparableOf(Class<I> type) {
    return asComparable(CrestFunctions.cast(type));
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparable(Function<? super I, ? extends T> function) {
    return (S) new AsComparable<>(function);
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparableOf(Class<T> type, String methodName, Object... args) {
    return (S) asComparable(CrestFunctions.invoke(methodName, (Object[]) args).<T>andThen(CrestFunctions.cast(type)));
  }

  public static <I> AsString<I> asString() {
    return asString(CrestFunctions.stringify());
  }

  public static <I> AsString<I> asString(Function<? super I, ? extends String> function) {
    return new AsString<>(requireNonNull(function));
  }

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <I> AsString<I> asString(String methodName, Object... args) {
    return asString((Function<? super I, ? extends String>) CrestFunctions.invoke(methodName, args));
  }

  public static <I extends Collection<? extends E>, E> AsStream<I, E> asStream() {
    return new AsStream<>(CrestFunctions.stream());
  }

  public static <I extends Collection<?>> AsList<? super I, ?> asObjectList() {
    return asListOf(Object.class, CrestFunctions.collectionToList());
  }

  public static <I> AsList<? super I, ?> asObjectList(Function<? super I, ? extends List<Object>> function) {
    return asListOf(Object.class, function);
  }

  public static <I extends Collection<E>, E> AsList<I, E> asListOf(Class<E> type) {
    return asListOf(type, CrestFunctions.collectionToList());
  }

  public static <I, E> AsList<I, E> asListOf(@SuppressWarnings("unused") Class<E> type, Function<? super I, ? extends List<E>> function) {
    return new AsList<>(function);
  }

  public static <I extends Map, SELF extends AsMap<I, Object, Object, SELF>> SELF asMap() {
    return asMapOf(Object.class, Object.class, Printable.function("mapToMap", o -> new HashMap<>()));
  }

  public static <I, SELF extends AsMap<I, Object, Object, SELF>> SELF asMap(Function<? super I, ? extends Map<Object, Object>> function) {
    return asMapOf(Object.class, Object.class, function);
  }

  public static <I extends Map<K, V>, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType) {
    return asMapOf(keyType, valueType, CrestFunctions.identity());
  }

  @SuppressWarnings("unchecked")
  public static <I, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType, Function<? super I, ? extends Map<K, V>> function) {
    requireNonNull(keyType);
    requireNonNull(valueType);
    return (SELF) new AsMap<I, K, V, SELF>(function);
  }

  public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
    assertThat("", actual, matcher);
  }

  public static <T> void assertThat(String message, T actual, Matcher<? super T> matcher) {
    Assertion.assertThat(message, actual, matcher);
  }
}

