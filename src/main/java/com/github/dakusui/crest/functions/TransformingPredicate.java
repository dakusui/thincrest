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

  public Predicate<? super P> predicate() {
    return this.predicate;
  }

  public Function<? super O, ? extends P> function() {
    return this.function;
  }

  @Override
  public String toString() {
    return String.format("%s(x) %s", function, predicate);
  }
}
