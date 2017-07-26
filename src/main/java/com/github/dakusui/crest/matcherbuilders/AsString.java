package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.function.Function;

public class AsString<I> extends AsObject<I, String, AsString<I>> {
  public AsString(Function<? super I, ? extends String> function) {
    super(function);
  }

  public AsString<I> matchesRegex(String regex) {
    return this.check(CrestPredicates.matchesRegex(regex));
  }

  public AsString<I> containsString(String string) {
    return this.check(CrestPredicates.containsString(string));

  }
}
