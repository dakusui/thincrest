package com.github.dakusui.crest.core;

public interface Matcher<T> {
  boolean matches(T value, Assertion<? extends T> session);

  void describeExpectation(Assertion<? extends T> session);

  void describeMismatch(Assertion<? extends T> session);

  interface Conjunctive<T> extends Matcher<T> {

  }

  interface Disjunctive<T> extends Matcher<T> {

  }
}
