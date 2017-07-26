package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.predicates.CrestPredicates;

import java.util.function.Function;
import java.util.function.Predicate;

public class AsComparable<I, T extends Comparable<T>> extends AsObject<I, AsComparable<I, T>> {
  public AsComparable(Function<? super I, ? extends T> function) {
    super(function);
  }

  @SuppressWarnings("unchecked")
  public AsComparable<I, T> gt(T value) {
    return this.check((Predicate<? super Object>) CrestPredicates.gt(value));
  }

  @SuppressWarnings("unchecked")
  public AsComparable<I, T> ge(T value) {
    return this.check((Predicate<? super Object>) CrestPredicates.ge(value));
  }

  @SuppressWarnings("unchecked")
  public AsComparable<I, T> lt(T value) {
    return this.check((Predicate<? super Object>) CrestPredicates.lt(value));
  }

  @SuppressWarnings("unchecked")
  public AsComparable<I, T> le(T value) {
    return this.check((Predicate<? super Object>) CrestPredicates.le(value));
  }

  @SuppressWarnings("unchecked")
  public AsComparable<I, T> eq(T value) {
    return this.check((Predicate<? super Object>) CrestPredicates.eq(value));
  }
}
