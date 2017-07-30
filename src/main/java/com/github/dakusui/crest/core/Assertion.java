package com.github.dakusui.crest.core;

import org.junit.ComparisonFailure;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.core.Precondition.ARGUMENT;
import static com.github.dakusui.crest.functions.CrestPredicates.isNotNull;

/**
 * Represents one invocation of 'assertThat' method.
 *
 * @param <T> Type of object to be verified.
 */
public interface Assertion<T> {
  void perform(T value);

  default <I> boolean test(Predicate<? super I> predicate, I value) {
    return predicate.test(value);
  }

  default <I, O> O apply(Function<? super I, ? extends O> function, I value) {
    return function.apply(value);
  }

  static <T> void assertThat(T value, Matcher<? super T> matcher) {
    create(matcher).perform(value);
  }

  static <T> Assertion<T> create(Matcher<? super T> matcher) {
    return new Impl<T>(matcher);
  }

  class Impl<T> implements Assertion<T> {
    private final Matcher<? super T> matcher;

    public Impl(Matcher<? super T> matcher) {
      this.matcher = Precondition.require(ARGUMENT, matcher, isNotNull());
    }

    @Override
    public void perform(T value) {
      this.matcher.describeExpectation(this);
      if (!this.matcher.matches(value, this))
        this.matcher.describeMismatch(this);
      else
        throwComparisonFailure(this);
    }

    private static <T> void throwComparisonFailure(Impl<T> self) {
      String message = null;
      String expectation = null;
      String actual = null;
      throw new ComparisonFailure(message, expectation, actual);
    }
  }
}
