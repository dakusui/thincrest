package com.github.dakusui.crest.core;

import com.github.dakusui.crest.functions.TransformingPredicate;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  <I, O> Optional<Throwable> thrownExceptionFor(Function<? super I, ? extends O> function, I value);

  static <T> void assertThat(String message, T value, Matcher<? super T> matcher) {
    new Impl<>(message, matcher).perform(value);
  }

  static <T> void assumeThat(String message, T value, Matcher<? super T> matcher) {
    new Impl<T>(message, matcher) {
      @Override
      void failedOnComparison(String message, String expected, String actual) {
        Throwable t = new ComparisonFailure(message, expected, actual);
        throw new AssumptionViolatedException(t.getMessage(), t);
      }
    }.perform(value);
  }

  static <T> void requireThat(String message, T value, Matcher<? super T> matcher) {
    new Impl<T>(message, matcher) {
      @Override
      void failedOnComparison(String message, String expected, String actual) {
        throw new ExecutionFailure(message, expected, actual, this.exceptions());
      }
    }.perform(value);
  }

  List<Throwable> exceptions();

  class Impl<T> implements Assertion<T> {
    private final Matcher<? super T>       matcher;
    private final Map<Predicate, Function> predicates = new HashMap<>();
    private final Map<Function, Function>  functions  = new HashMap<>();
    private final String                   messageOnFailure;
    private final List<Throwable>          exceptions = new LinkedList<>();

    public Impl(String messageOnFailure, Matcher<? super T> matcher) {
      this.messageOnFailure = messageOnFailure; // this can be null
      this.matcher = requireNonNull(matcher);
    }

    @Override
    public void perform(T value) {
      if (!matches(value))
        if (exceptions.isEmpty()) {
          failedOnComparison(value);
        } else {
          failedOnMatching(value);
        }
    }

    @Override
    public boolean test(Predicate<Object> predicate, Object value) {
      Object ret = applyPredicate(predicate, value);
      return ret instanceof Boolean ? (Boolean) ret : false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O apply(Function<? super I, ? extends O> function, I value) {
      return applyFunction(function, value);
    }

    @Override
    public <I> Optional<Throwable> thrownExceptionFor(Predicate<? super I> predicate, I value) {
      Object ret = applyPredicate(predicate, value);
      if (ret instanceof ExceptionHolder)
        return Optional.of(((ExceptionHolder) ret).get());
      return Optional.empty();
    }

    @Override
    public <I, O> Optional<Throwable> thrownExceptionFor(Function<? super I, ? extends O> function, I value) {
      Object ret = applyFunction(function, value);
      if (ret instanceof ExceptionHolder)
        return Optional.of(((ExceptionHolder) ret).get());
      return Optional.empty();
    }

    @Override
    public List<Throwable> exceptions() {
      return this.exceptions;
    }

    void failedOnComparison(String message, String expected, String actual) {
      throw new ComparisonFailure(message, expected, actual);
    }

    void failedOnMatching(String message, String expected, String actual) {
      throw new ExecutionFailure(message, expected, actual, this.exceptions);
    }

    private void failedOnComparison(T value) {
      failedOnComparison(
          messageOnFailure,
          String.join("\n", matcher.describeExpectation(this)),
          String.join("\n", matcher.describeMismatch(value, this))
      );
    }

    private void failedOnMatching(T value) {
      failedOnMatching(
          messageOnFailure,
          String.join("\n", matcher.describeExpectation(this)),
          String.join("\n", matcher.describeMismatch(value, this))
      );
    }

    @SuppressWarnings("unchecked")
    private <I, O> O applyFunction(Function<? super I, ? extends O> function, I value) {
      return ((Function<I, O>) functions.computeIfAbsent(function, this::memoize)).apply(value);
    }

    @SuppressWarnings("unchecked")
    private <I> Object applyPredicate(Predicate<? super I> predicate, I value) {
      return predicates.computeIfAbsent(predicate, this::memoize).apply(value);
    }

    private <I, O> Object tryToApply(Function<? super I, ? extends O> function, I value) {
      try {
        return function.apply(value);
      } catch (Exception e) {
        exceptions.add(e);
        return ExceptionHolder.create(exceptions);
      }
    }

    @SuppressWarnings("unchecked")
    private <I> Object tryToTest(Predicate<? super I> predicate, I value) {
      try {
        if (predicate instanceof TransformingPredicate) {
          TransformingPredicate pp = (TransformingPredicate) predicate;
          return tryToTest(pp.predicate(), apply(pp.function(), value));
        }
        return predicate.test(value);
      } catch (Exception e) {
        exceptions.add(e);
        return ExceptionHolder.create(exceptions);
      }
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private <I> Function<? super I, Object> memoize(Predicate<? super I> predicate) {
      Map<I, Object> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, v -> tryToTest(predicate, v));
    }

    @SuppressWarnings("unchecked")
    private <I, O> Function<I, O> memoize(Function<I, O> function) {
      Map<I, O> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i, v -> (O) tryToApply(function, v));
    }

    private boolean matches(T value) {
      return this.matcher.matches(value, this);
    }

    private interface ExceptionHolder extends Supplier<Throwable> {
      static ExceptionHolder create(List<Throwable> t) {
        requireNonNull(t);
        if (t.isEmpty())
          throw new RuntimeException();
        if (t.size() == 1)
          return () -> requireNonNull(t.get(0));
        return () -> new Error(t.get(0));
      }
    }
  }
}
