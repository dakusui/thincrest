package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsChar<I> extends AsComparable<I, Character, AsChar<I>> {
  public AsChar(Function<? super I, ? extends Character> function) {
    super(function);
  }
}
