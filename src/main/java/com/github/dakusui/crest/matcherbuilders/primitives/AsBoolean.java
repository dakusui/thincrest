package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.functions.CrestPredicates;
import com.github.dakusui.crest.matcherbuilders.ObjectMatcherBuilder;

import java.util.function.Function;

public class AsBoolean<IN> extends ObjectMatcherBuilder<IN, Boolean, AsBoolean<IN>> {
  public AsBoolean(Function<? super IN, ? extends Boolean> function) {
    super(function);
  }

  public AsBoolean<? super IN> isTrue() {
    this.check(CrestPredicates.isTrue());
    return this;
  }

  public AsBoolean<? super IN> isFalse() {
    this.check(CrestPredicates.isFalse());
    return this;
  }
}
