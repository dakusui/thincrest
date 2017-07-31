package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AsStream<IN, ENTRY> extends AsObject<IN, Stream<? extends ENTRY>, AsStream<IN, ENTRY>> {
  public AsStream(Function<? super IN, Stream<? extends ENTRY>> function) {
    super(function);
  }

  public AsStream<? super IN, ? extends ENTRY> allMatch(Predicate<? super ENTRY> predicate) {
    return this.check(CrestPredicates.allMatch(predicate));
  }

  public AsStream<? super IN, ? extends ENTRY> noneMatch(Predicate<? super ENTRY> predicate) {
    return this.check(CrestPredicates.noneMatch(predicate));
  }

  public AsStream<? super IN, ? extends ENTRY> anyMatch(Predicate<? super ENTRY> predicate) {
    return this.check(CrestPredicates.anyMatch(predicate));
  }
}
