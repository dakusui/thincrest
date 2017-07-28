package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.Objects;
import java.util.function.Function;

public class AsString<I> extends AsComparable<I, String, AsString<I>> {
  public AsString(Function<? super I, ? extends String> function) {
    super(function);
  }

  public AsString<I> matchesRegex(String regex) {
    return this.check(CrestPredicates.matchesRegex(Objects.requireNonNull(regex)));
  }

  public AsString<I> containsString(String string) {
    return this.check(CrestPredicates.containsString(Objects.requireNonNull(string)));

  }

  public AsString<I> startsWith(String s) {
    return this.check(CrestPredicates.startsWith(Objects.requireNonNull(s)));
  }

  public AsString<I> endsWith(String s) {
    return this.check(CrestPredicates.endsWith(Objects.requireNonNull(s)));
  }

  public AsString<I> equalsIgnoreCase(String s) {
    return this.check(CrestPredicates.equalsIgnoreCase(Objects.requireNonNull(s)));
  }

  public AsString<I> isEmpty() {
    return this.check(CrestPredicates.isEmptyString());
  }

  public AsString<I> isEmptyOrNull() {
    return this.check(CrestPredicates.isEmptyOrNullString());
  }
}
