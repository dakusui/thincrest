package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsLong<I> extends AsComparable<I, Long, AsLong<I>> {
  public AsLong(Function<? super I, ? extends Long> function) {
    super(function);
  }
}
