package com.github.dakusui.crest.core;

public interface Session<T> {

  default void perform(String message, T value) throws Throwable {
    if (!matcher().matches(value, this)) {
      matcher().describeExpectation(this);
      matcher().describeMismatch(value, this);
    }
  }

  Matcher<T> matcher();
}
