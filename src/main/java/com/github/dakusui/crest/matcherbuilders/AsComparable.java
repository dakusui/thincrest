package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;

public class AsComparable<I, T extends Comparable<? super T>> extends AsObject<I, T, AsComparable<I, T>> {
  public AsComparable(Function<? super I, ? extends T> function) {
    super(function);
  }

  public AsComparable<? super I, T> gt(T value) {
    return this.check(CrestPredicates.gt(value));
  }

  public AsComparable<? super I, T> ge(T value) {
    return this.check(CrestPredicates.ge(value));
  }

  public AsComparable<? super I, T> lt(T value) {
    return this.check(CrestPredicates.lt(value));
  }

  public AsComparable<? super I, T> le(T value) {
    return this.check(CrestPredicates.le(value));
  }

  public AsComparable<? super I, T> eq(T value) {
    return this.check(CrestPredicates.eq(value));
  }
}
