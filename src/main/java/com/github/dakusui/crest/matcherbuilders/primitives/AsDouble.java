package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsDouble<I> extends AsComparable<I, Double, AsDouble<I>> {
  public AsDouble(Function<? super I, ? extends Double> function) {
    super(function);
  }
}
