package com.github.dakusui.crest.matcherbuilders;

import java.util.List;
import java.util.function.Function;

public class AsList<I, E> extends AsObject<I, AsList<I, E>> {
  public AsList(Function<? super I, ? extends List<? extends E>> function) {
    super(function);
  }
}
