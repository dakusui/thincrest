package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Printable;
import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListMatcherBuilder<IN, ENTRY, SELF extends ListMatcherBuilder<IN, ENTRY, SELF>> extends ObjectMatcherBuilder<IN, List<ENTRY>, SELF> {
  protected ListMatcherBuilder(Function<? super IN, ? extends List<ENTRY>> function) {
    super(function);
  }

  public SELF containsAll(Collection<?> collection) {
    return this.check(CrestPredicates.containsAll(collection));
  }

  public SELF containsOnly(Collection<?> collection) {
    return this.check(CrestPredicates.containsOnly(collection));
  }

  public SELF containsExactly(Collection<?> collection) {
    return this.check(CrestPredicates.containsExactly(collection));
  }

  public SELF contains(Object entry) {
    return this.check(CrestPredicates.contains(entry));
  }

  public SELF isEmpty() {
    return this.check(CrestPredicates.isEmpty());
  }

  public SELF allMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("allMatch[%s]", predicate),
            entries -> entries.stream().allMatch(predicate)
        ));
  }

  public SELF anyMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("anyMatch[%s]", predicate),
            entries -> entries.stream().anyMatch(predicate)
        ));
  }

  public SELF noneMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Printable.predicate(
            String.format("noneMatch[%s]", predicate),
            entries -> entries.stream().noneMatch(predicate)
        ));
  }

}
