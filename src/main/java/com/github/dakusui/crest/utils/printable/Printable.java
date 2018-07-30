package com.github.dakusui.crest.utils.printable;


import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Printable {
  ;
  static boolean assertsEnabled = false;

  static {
    //noinspection ConstantConditions,AssertWithSideEffects
    assert assertsEnabled = true; // Intentional side-effect!!!
  }

  public static <T> Predicate<T> predicate(Supplier<String> s, Predicate<T> predicate) {
    return assertsEnabled ?
        Printable.printablePredicate(s.get(), predicate) :
        predicate;
  }

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return assertsEnabled ?
        Printable.printablePredicate(s, predicate) :
        predicate;
  }

  public static <T, R> Function<T, R> function(Supplier<String> s, Function<T, R> function) {
    return assertsEnabled ?
        Printable.printableFunction(s.get(), function) :
        function;
  }

  public static <T, R> Function<T, R> function(String s, Function<T, R> function) {
    return assertsEnabled ?
        Printable.printableFunction(s, function) :
        function;
  }

  @SuppressWarnings("unchecked")
  public static <T> Predicate<T> printablePredicate(String s, Predicate<T> predicate) {
    return (Predicate<T>) new PrintablePredicate(s, predicate);
  }

  public static <T, R> Function<T, R> printableFunction(String s, Function<? super T, ? extends R> function) {
    return new PrintableFunction<>(s, function);
  }
}