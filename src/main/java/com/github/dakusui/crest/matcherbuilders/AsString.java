package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestPredicates;

import java.util.Objects;
import java.util.function.Function;

public class AsString<IN> extends AsComparable<IN, String, AsString<IN>> {
  public AsString(Function<? super IN, ? extends String> function) {
    super(function);
  }

  public AsString<IN> matchesRegex(String regex) {
    return this.check(CrestPredicates.matchesRegex(Objects.requireNonNull(regex)));
  }

  public AsString<IN> containsString(String string) {
    return this.check(CrestPredicates.containsString(Objects.requireNonNull(string)));

  }

  public AsString<IN> startsWith(String s) {
    return this.check(CrestPredicates.startsWith(Objects.requireNonNull(s)));
  }

  public AsString<IN> endsWith(String s) {
    return this.check(CrestPredicates.endsWith(Objects.requireNonNull(s)));
  }

  public AsString<IN> equalsIgnoreCase(String s) {
    return this.check(CrestPredicates.equalsIgnoreCase(Objects.requireNonNull(s)));
  }

  public AsString<IN> isEmpty() {
    return this.check(CrestPredicates.isEmptyString());
  }

  public AsString<IN> isEmptyOrNull() {
    return this.check(CrestPredicates.isEmptyOrNullString());
  }
}
