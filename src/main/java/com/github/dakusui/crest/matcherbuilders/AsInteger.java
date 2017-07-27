package com.github.dakusui.crest.matcherbuilders;

import java.util.function.Function;

public class AsInteger<I> extends AsComparable<I, Integer> {
  public AsInteger(Function<? super I, ? extends Integer> function) {
    super(function);
  }
}
