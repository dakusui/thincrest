package com.github.dakusui.crest.matcherbuilders.primitives;

import com.github.dakusui.crest.matcherbuilders.AsComparable;

import java.util.function.Function;

public class AsByte<I> extends AsComparable<I, Byte, AsByte<I>> {
  public AsByte(Function<? super I, ? extends Byte> function) {
    super(function);
  }
}
