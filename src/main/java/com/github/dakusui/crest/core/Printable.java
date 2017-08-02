package com.github.dakusui.crest.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Printable {
  ;

  public static <T, R> Function<T, R> function(String s, Function<? super T, ? extends R> function) {
    Objects.requireNonNull(s);
    Objects.requireNonNull(function);
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return new Function<V, R>() {
          @Override
          public R apply(V v) {
            return function.apply(before.apply(v));
          }

          @Override
          public String toString() {
            return String.format("%s->%s", before, s);
          }
        };
      }

      public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return new Function<T, V>() {
          @Override
          public V apply(T t) {
            return after.apply(function.apply(t));
          }

          @Override
          public String toString() {
            return String.format("%s->%s", s, after);
          }
        };
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T> Predicate<T> predicate(String s, Predicate<? super T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return predicate.test(t) && other.test(t);
          }

          @Override
          public String toString() {
            return String.format("(%s&&%s)", s, other);
          }
        };
      }

      @Override
      public Predicate<T> negate() {
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return !predicate.test(t);
          }

          @Override
          public String toString() {
            return String.format("!%s", s);
          }
        };
      }

      @Override
      public Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return predicate.test(t) || other.test(t);
          }

          @Override
          public String toString() {
            return String.format("(%s||%s)", s, other);
          }
        };
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }
}
