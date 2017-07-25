package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.CrestPredicates;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AsStream<E> extends AsObject<Collection<E>, Stream<? extends E>> {
  public AsStream(Function<? super Collection<? extends E>, Stream<? extends E>> function) {
    super(function);
  }

  public AsStream<E> allMatch(Predicate<E> predicate) {
    return this.check(CrestPredicates.allMatch(predicate));
  }

  public AsStream<E> noneMatch(Predicate<? super E> predicate) {
    return this.check(CrestPredicates.noneMatch(predicate));
  }

  public AsStream<E> anyMatch(Predicate<E> predicate) {
    return this.check(CrestPredicates.anyMatch(predicate));
  }
}
