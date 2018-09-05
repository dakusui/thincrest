package com.github.dakusui.crest.functions;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class TransformingPredicate<P, O> implements Predicate<O> {
  private final Predicate<? super P>             predicate;
  private final Function<? super O, ? extends P> function;
  private       String                           name;

  public TransformingPredicate(Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this(null, predicate, function);
  }

  public TransformingPredicate(String name, Predicate<? super P> predicate, Function<? super O, ? extends P> function) {
    this.name = name;
    this.predicate = predicate;
    this.function = function;
  }

  @Override
  public boolean test(O v) {
    ////
    // This method is usually not called. Because Assertion class invokes function
    // and predicate of this object by itself and do not use this method.
    return predicate.test(function.apply(v));
  }

  public Predicate<? super P> predicate() {
    return this.predicate;
  }

  public Function<? super O, ? extends P> function() {
    return this.function;
  }

  public Optional<String> name() {
    return name != null ?
        Optional.of(this.name) :
        Optional.empty();
  }

  @Override
  public String toString() {
    return String.format("%s %s", function(), predicate());
  }
}

