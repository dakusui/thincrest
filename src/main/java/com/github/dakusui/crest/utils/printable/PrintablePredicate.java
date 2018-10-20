package com.github.dakusui.crest.utils.printable;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class PrintablePredicate<T> implements Predicate<T> {
  private final String       s;
  private final Predicate<T> predicate;

  PrintablePredicate(String s, Predicate<T> predicate) {
    this.s = s;
    this.predicate = predicate;
  }

  @Override
  public boolean test(T t) {
    return predicate.test(t);
  }

  @Override
  public Predicate<T> and(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<T>(String.format("(%s&&%s)", s, other), predicate.and(other));
  }

  @Override
  public Predicate<T> negate() {
    return new PrintablePredicate<T>(String.format("!%s", s), predicate.negate());
  }

  @Override
  public Predicate<T> or(Predicate<? super T> other) {
    requireNonNull(other);
    return new PrintablePredicate<T>(String.format("(%s||%s)", s, other), predicate.or(other));
  }

  @Override
  public String toString() {
    return s;
  }
}
