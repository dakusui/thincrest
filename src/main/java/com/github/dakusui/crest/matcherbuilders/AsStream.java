package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.predicates.CrestPredicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AsStream<I, E> extends AsObject<I> {
  public AsStream(Function<I, Stream<? extends E>> function) {
    super(function);
  }

  @SuppressWarnings("unchecked")
  public AsStream<? super I, ? extends E> allMatch(Predicate<E> predicate) {
    return this.check((Predicate<? super Object>) CrestPredicates.allMatch(predicate));
  }

  @SuppressWarnings("unchecked")
  public AsStream<? super I, ? extends E> noneMatch(Predicate<? super E> predicate) {
    return this.check((Predicate<? super Object>) CrestPredicates.noneMatch(predicate));
  }

  @SuppressWarnings("unchecked")
  public AsStream<? super I, ? extends E> anyMatch(Predicate<E> predicate) {
    return this.check((Predicate<? super Object>) CrestPredicates.anyMatch(predicate));
  }
}
