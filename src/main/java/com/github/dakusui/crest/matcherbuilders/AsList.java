package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AsList<IN, ENTRY> extends AsObject<IN, List<ENTRY>, AsList<IN, ENTRY>> {
  public AsList(Function<? super IN, ? extends List<ENTRY>> function) {
    super(function);
  }

  public AsList<? super IN, ? extends ENTRY> containsAll(Collection<?> collection) {
    return this.check(CrestPredicates.containsAll(collection));
  }

  public AsList<? super IN, ? extends ENTRY> containsOnly(Collection<?> collection) {
    return this.check(CrestPredicates.containsOnly(collection));
  }

  public AsList<? super IN, ? extends ENTRY> containsExactly(Collection<?> collection) {
    return this.check(CrestPredicates.containsExactly(collection));
  }

  public AsList<? super IN, ? extends ENTRY> contains(Object entry) {
    return this.check(CrestPredicates.contains(entry));
  }

  public AsList<? super IN, ? extends ENTRY> isEmpty() {
    return this.check(CrestPredicates.isEmpty());
  }

  public AsList<? super IN, ? extends ENTRY> allMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Formattable.predicate(
            String.format("allMatch[%s]", predicate),
            entries -> entries.stream().allMatch(predicate)
        ));
  }

  public AsList<? super IN, ? extends ENTRY> anyMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Formattable.predicate(
            String.format("anyMatch[%s]", predicate),
            entries -> entries.stream().anyMatch(predicate)
        ));
  }

  public AsList<? super IN, ? extends ENTRY> noneMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Formattable.predicate(
            String.format("noneMatch[%s]", predicate),
            entries -> entries.stream().noneMatch(predicate)
        ));
  }
}
