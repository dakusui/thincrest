package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.CrestPredicates;

import java.util.function.Function;

public class AsString<I> extends AsObject<I, String> {
  public AsString(Function<? super I, ? extends String> function) {
    super(function);
  }

  public AsString<I> matchesRegex(String regex) {
    this.check(CrestPredicates.matchesRegex(regex));
    return this;
  }

  public AsString<I> containsString(String string) {
    this.check(CrestPredicates.containsString(string));
    return this;
  }
}
