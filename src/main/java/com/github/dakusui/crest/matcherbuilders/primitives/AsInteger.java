package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsInteger<I> extends AsComparable<I, Integer, AsInteger<I>> {
  public AsInteger(Function<? super I, ? extends Integer> function) {
    super(function);
  }
}
