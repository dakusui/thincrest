package com.github.dakusui.crest.functions;

import java.util.function.Function;
import java.util.function.Predicate;

public class TransformingPredicate<P, O> implements Predicate<O> {
  private final Predicate<? super P>             predicate;
  private final Function<? super O, ? extends P> function;

  public TransformingPredicate(Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this.predicate = predicate;
    this.function = function;
  }

  @Override
  public boolean test(O v) {
    return predicate.test(function.apply(v));
  }

  @Override
  public String toString() {
    return String.format("%s%s", predicate, function);
  }
}
