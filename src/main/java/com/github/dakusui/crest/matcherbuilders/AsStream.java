package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AsStream<I, E> extends AsObject<I, Stream<? extends E>, AsStream<I, E>> {
  public AsStream(Function<? super I, Stream<? extends E>> function) {
    super(function);
  }

  public AsStream<? super I, ? extends E> allMatch(Predicate<? super E> predicate) {
    return this.check(CrestPredicates.allMatch(predicate));
  }

  public AsStream<? super I, ? extends E> noneMatch(Predicate<? super E> predicate) {
    return this.check(CrestPredicates.noneMatch(predicate));
  }

  public AsStream<? super I, ? extends E> anyMatch(Predicate<? super E> predicate) {
    return this.check(CrestPredicates.anyMatch(predicate));
  }
}
