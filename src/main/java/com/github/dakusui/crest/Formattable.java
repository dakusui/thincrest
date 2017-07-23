package com.github.dakusui.crest;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public enum Formattable {
  ;

  public static <I, O> Function<I, O> function(String s, Function<I, O> function) {
    Objects.requireNonNull(s);
    Objects.requireNonNull(function);
    return new Function<I, O>() {
      @Override
      public O apply(I i) {
        return function.apply(i);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <I> Predicate<I> predicate(String s, Predicate<I> predicate) {
    return new Predicate<I>() {
      @Override
      public boolean test(I i) {
        return predicate.test(i);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static void main(String... args) {
    System.out.println(function("Hello(x)", x -> "Hello:" + x));
  }
}
