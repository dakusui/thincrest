package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;

public class AsComparable<IN, OF_TYPE extends Comparable<? super OF_TYPE>,
    SELF extends AsComparable<IN, OF_TYPE, SELF>> extends ObjectMatcherBuilder<IN, OF_TYPE, SELF> {
  public AsComparable(Function<? super IN, ? extends OF_TYPE> function) {
    super(function);
  }

  @SuppressWarnings("unchecked")
  public <S extends SELF> S gt(OF_TYPE value) {
    return (S) this.check(CrestPredicates.gt(value));
  }

  @SuppressWarnings("unchecked")
  public <S extends SELF> S ge(OF_TYPE value) {
    return (S) this.check(CrestPredicates.ge(value));
  }

  @SuppressWarnings("unchecked")
  public <S extends SELF> S lt(OF_TYPE value) {
    return (S) this.check(CrestPredicates.lt(value));
  }

  @SuppressWarnings("unchecked")
  public <S extends SELF> S le(OF_TYPE value) {
    return (S) this.check(CrestPredicates.le(value));
  }

  @SuppressWarnings("unchecked")
  public <S extends SELF> S eq(OF_TYPE value) {
    return (S) this.check(CrestPredicates.eq(value));
  }
}
