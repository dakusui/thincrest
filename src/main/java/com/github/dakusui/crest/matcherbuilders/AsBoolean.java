package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;

public class AsBoolean<I> extends AsObject<I, Boolean, AsBoolean<I>> {
  public AsBoolean(Function<? super I, ? extends Boolean> function) {
    super(function);
  }

  public AsBoolean<? super I> isTrue() {
    this.check(CrestPredicates.isTrue());
    return this;
  }

  public AsBoolean<? super I> isFalse() {
    this.check(CrestPredicates.isFalse());
    return this;
  }
}
