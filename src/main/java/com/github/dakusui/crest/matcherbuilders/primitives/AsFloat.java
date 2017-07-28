package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsFloat<I> extends AsComparable<I, Float, AsFloat<I>> {
  public AsFloat(Function<? super I, ? extends Float> function) {
    super(function);
  }
}
