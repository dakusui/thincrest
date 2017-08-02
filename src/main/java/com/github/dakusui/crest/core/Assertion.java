package com.github.dakusui.crest.core;

import com.github.dakusui.crest.functions.TransformingPredicate;
import org.junit.ComparisonFailure;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents one invocation of 'assertThat' method.
 *
 * @param <T> Type of object to be verified.
 */
public interface Assertion<T> {
  void perform(T value);

  boolean test(Predicate<Object> predicate, Object value);

  <I, O> O apply(Function<? super I, ? extends O> function, I value);

  <I> Optional<Throwable> thrownExceptionFor(Predicate<? super I> predicate, I value);

  static <T> void assertThat(String message, T value, Matcher<? super T> matcher) {
    create(message, matcher).perform(value);
  }

  static <T> Assertion<T> create(String messageOnFailure, Matcher<? super T> matcher) {
    return new Impl<T>(messageOnFailure, matcher);
  }

  List<Throwable> exceptions();

  class Impl<T> implements Assertion<T> {
    private final Matcher<? super T> matcher;
    private final Map<Predicate, Function> predicates = new HashMap<>();
    private final Map<Function, Function>  functions  = new HashMap<>();
    private final String messageOnFailure;
    private final List<Throwable> exceptions = new LinkedList<>();

    Impl(String messageOnFailure, Matcher<? super T> matcher) {
      this.messageOnFailure = messageOnFailure; // this can be null
      this.matcher = requireNonNull(matcher);
    }

    @Override
    public void perform(T value) {
      if (!this.matcher.matches(value, this))
        throwComparisonFailure(messageOnFailure, value, matcher);
    }

    @Override
    public boolean test(Predicate<Object> predicate, Object value) {
      Object ret = applyPredicate(predicate, value);
      return ret instanceof Boolean ? (Boolean) ret : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O apply(Function<? super I, ? extends O> function, I value) {
      return ((Function<I, O>) functions.computeIfAbsent(function, this::memoize)).apply(value);
    }

    @Override
    public <I> Optional<Throwable> thrownExceptionFor(Predicate<? super I> predicate, I value) {
      Object ret = applyPredicate(predicate, value);
      if (ret instanceof ExceptionHolder)
        return Optional.of(((ExceptionHolder) ret).get());
      return Optional.empty();
    }

    @Override
    public List<Throwable> exceptions() {
      return this.exceptions;
    }

    @SuppressWarnings("unchecked")
    private <I> Object applyPredicate(Predicate<? super I> predicate, I value) {
      return predicates.computeIfAbsent(predicate, this::memoize).apply(value);
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private <I> Function<? super I, Object> memoize(Predicate<? super I> predicate) {
      Map<I, Object> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, v -> tryToTest(predicate, v));
    }

    private <I> Object tryToTest(Predicate<? super I> predicate, I value) {
      try {
        if (predicate instanceof TransformingPredicate) {
          TransformingPredicate pp = (TransformingPredicate) predicate;
          return tryToTest(pp.predicate(), apply(pp.function(), value));
        }
        return predicate.test(value);
      } catch (Exception e) {
        exceptions.add(e);
        return ExceptionHolder.create(e);
      }
    }

    private <I, O> Function<I, O> memoize(Function<I, O> function) {
      Map<I, O> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, function);
    }

    private void throwComparisonFailure(String messageOnFailure, T value, Matcher<? super T> matcher) {
      requireNonNull(matcher);
      throw new ComparisonFailure(
          messageOnFailure,
          String.join("\n", matcher.describeExpectation(this)),
          String.join("\n", matcher.describeMismatch(value, this))
      );
    }

    private interface ExceptionHolder extends Supplier<Exception> {
      static ExceptionHolder create(Exception t) {
        return () -> requireNonNull(t);
      }
    }
  }
}
