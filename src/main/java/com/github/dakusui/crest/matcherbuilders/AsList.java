package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class AsList<I, E> extends AsObject<I, List<E>, AsList<I, E>> {
  public AsList(Function<? super I, ? extends List<E>> function) {
    super(function);
  }

  public AsList<? super I, ? extends E> containsAll(Collection<? extends E> collection) {
    return this.check(CrestPredicates.containsAll(collection));
  }

  public AsList<? super I, ? extends E> contains(E entry) {
    return this.check(CrestPredicates.contains(entry));
  }

  public AsList<? super I, ? extends E> isEmpty() {
    return this.check(CrestPredicates.isEmpty());
  }
}
