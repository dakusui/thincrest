package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.predicates.CrestPredicates;

import java.util.function.Function;
import java.util.function.Predicate;

public class AsString<I> extends AsObject<I, AsString<I>> {
  public AsString(Function<? super I, ? extends String> function) {
    super(function);
  }

  @SuppressWarnings("unchecked")
  public AsString<I> matchesRegex(String regex) {
    return this.check((Predicate<Object>) CrestPredicates.matchesRegex(regex));
  }

  @SuppressWarnings("unchecked")
  public AsString<I> containsString(String string) {
    return this.check((Predicate<Object>) CrestPredicates.containsString(string));

  }
}
