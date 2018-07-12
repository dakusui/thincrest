package com.github.dakusui.crest;

import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.Call;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.core.Printable;
import com.github.dakusui.crest.matcherbuilders.*;
import com.github.dakusui.crest.matcherbuilders.primitives.*;
import com.github.dakusui.faultsource.printable.Functions;

import java.util.*;
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

  public static <T> Matcher<T> not(Matcher<? super T> matcher) {
    return Matcher.Negative.create(matcher);
  }


  @SuppressWarnings("unchecked")
  public static <T> Matcher<T> noneOf(Matcher... matcher) {
    return new Matcher.Composite.Base<T>(true, Arrays.asList(matcher)) {
      @Override
      protected String name() {
        return "noneOf";
      }

      @Override
      protected boolean first() {
        return true;
      }

      @Override
      protected boolean op(boolean current, boolean next) {
        return current && !next;
      }
    };
  }

  public static <I> AsObject<I, I> asObject() {
    return new AsObject<>(Functions.identity());
  }

  public static <I, O> AsObject<I, O> asObject(String methodName, Object... args) {
    return new AsObject<>(Functions.<I, O>invoke(methodName, args));
  }

  public static <I, O> AsObject<I, O> asObject(Function<? super I, ? extends O> function) {
    return new AsObject<>(function);
  }

  public static AsBoolean<Boolean> asBoolean() {
    return asBoolean(Functions.identity());
  }

  public static <I> AsBoolean<I> asBoolean(String methodName, Object... args) {
    return asBoolean(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Boolean.class)));
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
    return asByte(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Byte.class)));
  }

  public static AsByte<Byte> asByte() {
    return asByte(Functions.identity());
  }

  public static <I> AsChar<I> asChar(Function<? super I, Character> function) {
    return new AsChar<>(function);
  }

  public static <I> AsChar<I> asChar(String methodName, Object... args) {
    return asChar(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Character.class)));
  }

  public static AsChar<Character> asChar() {
    return asChar(Functions.identity());
  }

  public static <I> AsShort<I> asShort(Function<? super I, Short> function) {
    return new AsShort<>(function);
  }

  public static <I> AsShort<I> asShort(String methodName, Object... args) {
    return asShort(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Short.class)));
  }

  public static AsShort<Short> asShort() {
    return asShort(Functions.identity());
  }

  public static <I> AsInteger<I> asInteger(Function<? super I, Integer> function) {
    return new AsInteger<>(function);
  }

  public static <I> AsInteger<I> asInteger(String methodName, Object... args) {
    return asInteger(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Integer.class)));
  }

  public static AsInteger<Integer> asInteger() {
    return asInteger(Functions.identity());
  }

  public static <I> AsLong<I> asLong(Function<? super I, Long> function) {
    return new AsLong<>(function);
  }

  public static <I> AsLong<I> asLong(String methodName, Object... args) {
    return asLong(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Long.class)));
  }

  public static AsLong<Long> asLong() {
    return asLong(Functions.identity());
  }

  public static <I> AsFloat<I> asFloat(Function<? super I, Float> function) {
    return new AsFloat<>(function);
  }

  public static <I> AsFloat<I> asFloat(String methodName, Object... args) {
    return asFloat(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Float.class)));
  }

  public static AsFloat<Float> asFloat() {
    return asFloat(Functions.identity());
  }

  public static <I> AsDouble<I> asDouble(Function<? super I, Double> function) {
    return new AsDouble<>(function);
  }

  public static <I> AsDouble<I> asDouble(String methodName, Object... args) {
    return asDouble(Functions.invoke(methodName, (Object[]) args).andThen(Functions.cast(Double.class)));
  }

  public static AsDouble<Double> asDouble() {
    return asDouble(Functions.identity());
  }

  /*
   * Casts a given object into the given comparable type
   */
  public static <I extends Comparable<? super I>, S extends AsComparable<I, I, S>>
  S asComparableOf(Class<I> type) {
    return asComparable((Function<? super I, ? extends I>) Functions.cast(type));
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparable(Function<? super I, ? extends T> function) {
    return (S) new AsComparable<>(function);
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparableOf(Class<T> type, String methodName, Object... args) {
    return (S) asComparable(Functions.invoke(methodName, (Object[]) args).<T>andThen(Functions.cast(type)));
  }

  public static <I> AsString<I> asString() {
    return asString(Functions.stringify());
  }

  public static <I> AsString<I> asString(Function<? super I, ? extends String> function) {
    return new AsString<>(requireNonNull(function));
  }

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <I> AsString<I> asString(String methodName, Object... args) {
    return asString((Function<? super I, ? extends String>) Functions.invoke(methodName, args));
  }

  public static <I extends Collection<?>> AsList<? super I, ?> asObjectList() {
    return asListOf(Object.class, Functions.collectionToList());
  }

  public static <I> AsList<? super I, ?> asObjectList(Function<? super I, ? extends List<Object>> function) {
    return asListOf(Object.class, function);
  }

  public static <I extends Collection<E>, E> AsList<I, E> asListOf(Class<E> type) {
    return asListOf(type, Functions.collectionToList());
  }

  public static <I, E> AsList<I, E> asListOf(@SuppressWarnings("unused") Class<E> type, Function<? super I, ? extends List<E>> function) {
    return new AsList<>(function);
  }

  public static <I extends Map, SELF extends AsMap<I, Object, Object, SELF>> SELF asObjectMap() {
    return asMapOf(Object.class, Object.class, Printable.function("mapToMap", o -> new HashMap<>()));
  }

  public static <I, SELF extends AsMap<I, Object, Object, SELF>> SELF asObjectMap(Function<? super I, ? extends Map<Object, Object>> function) {
    return asMapOf(Object.class, Object.class, function);
  }

  public static <I extends Map<K, V>, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType) {
    return asMapOf(keyType, valueType, Functions.identity());
  }

  @SuppressWarnings("unchecked")
  public static <I, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType, Function<? super I, ? extends Map<K, V>> function) {
    requireNonNull(keyType);
    requireNonNull(valueType);
    return (SELF) new AsMap<I, K, V, SELF>(function);
  }

  public static Call call(String methodName, Object... args) {
    return Call.create(methodName, args);
  }

  public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
    assertThat("", actual, matcher);
  }

  public static <T> void assertThat(String message, T actual, Matcher<? super T> matcher) {
    Assertion.assertThat(message, actual, matcher);
  }
}

