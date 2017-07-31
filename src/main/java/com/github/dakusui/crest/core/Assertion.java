package com.github.dakusui.crest.core;

import org.junit.ComparisonFailure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.functions.CrestPredicates.isNotNull;

/**
 * Represents one invocation of 'assertThat' method.
 *
 * @param <T> Type of object to be verified.
 */
public interface Assertion<T> {
  void perform(T value);

  <I> boolean test(Predicate<? super I> predicate, I value);

  <I, O> O apply(Function<? super I, ? extends O> function, I value);

  static <T> void assertThat(String message, T value, Matcher<? super T> matcher) {
    create(message, matcher).perform(value);
  }

  static <T> void assertThat(T value, Matcher<? super T> matcher) {
    assertThat(null, value, matcher);
  }

  static <T> Assertion<T> create(String messageOnFailure, Matcher<? super T> matcher) {
    return new Impl<T>(messageOnFailure, matcher);
  }

  class Impl<T> implements Assertion<T> {
    private final Matcher<? super T> matcher;
    private final Map<Predicate<?>, Predicate<?>>     predicates = new HashMap<>();
    private final Map<Function<?, ?>, Function<?, ?>> functions  = new HashMap<>();
    private final String messageOnFailure;

    Impl(String messageOnFailure, Matcher<? super T> matcher) {
      this.messageOnFailure = messageOnFailure;
      this.matcher = Variable.Category.ARGUMENT.require("matcher", matcher, isNotNull());
    }

    @Override
    public void perform(T value) {
      if (!this.matcher.matches(value, this))
        throwComparisonFailure(messageOnFailure, value, matcher);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I> boolean test(Predicate<? super I> predicate, I value) {
      return ((Predicate<I>) predicates.computeIfAbsent(predicate, this::memoize)).test(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O apply(Function<? super I, ? extends O> function, I value) {
      return ((Function<I, O>) functions.computeIfAbsent(function, this::memoize)).apply(value);
    }

    private <I, O> Function<I, O> memoize(Function<I, O> function) {
      Map<I, O> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, function);
    }

    private <I> Predicate<I> memoize(Predicate<I> predicate) {
      Map<I, Boolean> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, predicate::test);
    }

    private void throwComparisonFailure(String messageOnFailure, T value, Matcher<? super T> matcher) {
      Objects.requireNonNull(matcher);
      throw new ComparisonFailure(
          messageOnFailure,
          String.join("\n", matcher.describeExpectation(this)),
          String.join("\n", matcher.describeMismatch(value, this))
      );
    }
  }
}
