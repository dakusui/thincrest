package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsShort<I> extends AsComparable<I, Short, AsShort<I>> {
  public AsShort(Function<? super I, ? extends Short> function) {
    super(function);
  }
}
