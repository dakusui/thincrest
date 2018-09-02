package com.github.dakusui.crest.core;

import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Report {
  static <T> Report perform(String message, T value, Matcher<T> matcher) throws Throwable {
    Session<T> session = Session.create();
    if (!matcher.matches(value, session)) {
      matcher.describeExpectation(session);
      matcher.describeMismatch(value, session);
    }
    return session.report();
  }

  String expectation();

  String mismatch();

  List<Throwable> exceptions();

  interface Session<T> {
    Report report();

    default void describeExpectation(Matcher.Composite<T> matcher) {
      beginExpectation(matcher);
      try {
        matcher.children().forEach(each -> each.describeExpectation(this));
      } finally {
        endExpectation(matcher);
      }
    }

    default void describeMismatch(T value, Matcher.Composite<T> matcher) {
      beginMisMatch(value, matcher);
      try {
        matcher.children().forEach(each -> each.describeMismatch(value, this));
      } finally {
        endMismatch(value, matcher);
      }
    }

    void beginExpectation(Matcher.Composite<T> matcher);

    void endExpectation(Matcher.Composite<T> matcher);

    void describeExpectation(Matcher.Leaf<T> matcher);

    void beginMisMatch(T value, Matcher.Composite<T> matcher);

    void endMismatch(T value, Matcher.Composite<T> matcher);

    void describeMismatch(T value, Matcher.Leaf<T> matcher);

    @SuppressWarnings("unchecked")
    default <X> boolean matches(Matcher.Leaf<T> leaf, T value) {
      return this.test(
          (Predicate<X>) leaf.p(),
          this.apply(
              (Function<T, X>) leaf.func(),
              value
          ));
    }

    <I, O> O apply(Function<I, O> func, I value);

    <I> boolean test(Predicate<I> pred, I value);

    static <T> Session<T> create() {
      return new Session.Impl<>();
    }

    class Impl<T> implements Session<T> {
      Map<Function, Function>   memoizationMapForFunctions  = new HashMap<>();
      Map<Predicate, Predicate> memoizationMapForPredicates = new HashMap<>();

      @Override
      public Report report() {
        return new Report() {
          @Override
          public String expectation() {
            return null;
          }

          @Override
          public String mismatch() {
            return null;
          }

          @Override
          public List<Throwable> exceptions() {
            return null;
          }
        };
      }

      @Override
      public void beginExpectation(Matcher.Composite<T> matcher) {

      }

      @Override
      public void endExpectation(Matcher.Composite<T> matcher) {

      }

      @Override
      public void describeExpectation(Matcher.Leaf<T> matcher) {

      }

      @Override
      public void beginMisMatch(T value, Matcher.Composite<T> matcher) {

      }

      @Override
      public void endMismatch(T value, Matcher.Composite<T> matcher) {

      }

      @Override
      public void describeMismatch(T value, Matcher.Leaf<T> matcher) {

      }

      @Override
      public <I, O> O apply(Function<I, O> func, I value) {
        return null;
      }

      @Override
      public <I> boolean test(Predicate<I> pred, I value) {
        return pred.test(value);
      }

      @SuppressWarnings("unchecked")
      private <I, O> Function<I, O> memoizedFunction(Function<I, O> function) {
        return memoizationMapForFunctions.computeIfAbsent(function, this::memoize);
      }

      private <I, O> Function<I, O> memoize(Function<I, O> function) {
        Map<I, Supplier<O>> memo = new HashMap<>();
        return i -> memo.computeIfAbsent(i,
            k -> {
              Throwable throwable;
              try {
                O result = function.apply(k);
                return () -> result;
              } catch (RuntimeException | Error e) {
                throwable = e;
                return () -> {
                  if (throwable instanceof RuntimeException)
                    throw (RuntimeException) throwable;
                  throw (Error) throwable;
                };
              }
            }
        ).get();
      }
    }
  }
}
