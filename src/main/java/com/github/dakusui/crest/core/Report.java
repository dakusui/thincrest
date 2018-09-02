package com.github.dakusui.crest.core;

import org.junit.ComparisonFailure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Report {
  static <T> void assertThat(String message, T value, Matcher<? super T> matcher) {
    Report report = perform(value, matcher);
    if (!report.wasSuccessful()) {
      if (report.exceptions().isEmpty())
        throw new ComparisonFailure(message, report.expectation(), report.mismatch());
      throw new ExecutionFailure(message, report.expectation(), report.mismatch());
    }
  }

  static <T> Report perform(T value, Matcher<T> matcher) {
    Session<T> session = Session.create();
    if (matcher.matches(value, session)) {
      session.matched(true);
    } else {
      matcher.describeExpectation(session.matched(false));
      matcher.describeMismatch(value, session);
    }
    return session.report();
  }

  String expectation();

  String mismatch();

  List<Throwable> exceptions();

  boolean wasSuccessful();

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
      beginMismatch(value, matcher);
      try {
        matcher.children().forEach(each -> each.describeMismatch(value, this));
      } finally {
        endMismatch(value, matcher);
      }
    }

    void beginExpectation(Matcher.Composite<T> matcher);

    void endExpectation(Matcher.Composite<T> matcher);

    void describeExpectation(Matcher.Leaf<T> matcher);

    void beginMismatch(T value, Matcher.Composite<T> matcher);

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

    Session<T> matched(boolean b);

    class Impl<T> implements Session<T> {
      Map<Function, Function>   memoizationMapForFunctions  = new HashMap<>();
      Map<Predicate, Predicate> memoizationMapForPredicates = new HashMap<>();

      Writer expectationWriter = new Writer();
      Writer mismatchWriter    = new Writer();

      List<Throwable> exceptions = new LinkedList<>();

      private boolean result;

      class Writer {
        int          level  = 0;
        List<String> buffer = new LinkedList<>();

        Writer enter() {
          level++;
          return this;
        }

        Writer leave() {
          level--;
          return this;
        }

        Writer appendLine(String format, Object... args) {
          buffer.add(indent(this.level) + String.format(format, args));
          return this;
        }

        String write() {
          return this.buffer.stream().collect(Collectors.joining("\n"));
        }

        private String indent(int level) {
          StringBuilder builder = new StringBuilder();
          for (int i = 0; i < level; i++) {
            builder.append("  ");
          }
          return builder.toString();
        }
      }

      @Override
      public Report report() {
        return new Report() {
          private boolean result = Impl.this.result;
          private String mismatch = mismatchWriter.write();
          private String expectation = expectationWriter.write();

          @Override
          public String expectation() {
            return expectation;
          }

          @Override
          public String mismatch() {
            return mismatch;
          }

          @Override
          public List<Throwable> exceptions() {
            return null;
          }

          @Override
          public boolean wasSuccessful() {
            return result;
          }
        };
      }

      @Override
      public void beginExpectation(Matcher.Composite<T> matcher) {
        expectationWriter.enter();
      }

      @Override
      public void endExpectation(Matcher.Composite<T> matcher) {
        expectationWriter.leave();
      }

      @Override
      public void describeExpectation(Matcher.Leaf<T> matcher) {

      }

      @Override
      public void beginMismatch(T value, Matcher.Composite<T> matcher) {
        mismatchWriter.enter();
      }

      @Override
      public void endMismatch(T value, Matcher.Composite<T> matcher) {
        mismatchWriter.leave();
      }

      @Override
      public void describeMismatch(T value, Matcher.Leaf<T> matcher) {

      }

      @SuppressWarnings("unchecked")
      @Override
      public <I, O> O apply(Function<I, O> func, I value) {
        if (func instanceof Call.ChainedFunction) {
          Call.ChainedFunction cf = (Call.ChainedFunction) func;
          return (O) apply(cf.chained(), apply(cf.previous(), value));
        }
        return memoizedFunction(func).apply(value);
      }

      @Override
      public <I> boolean test(Predicate<I> pred, I value) {
        return memoizedPredicate(pred).test(value);
      }

      @Override
      public Session matched(boolean b) {
        this.result = b;
        return this;
      }

      private void addException(Throwable throwable) {
        this.exceptions.add(throwable);
      }

      @SuppressWarnings("unchecked")
      private <I, O> Function<I, O> memoizedFunction(Function<I, O> function) {
        return memoizationMapForFunctions.computeIfAbsent(function, this::memoize);
      }

      @SuppressWarnings("unchecked")
      private <I> Predicate<I> memoizedPredicate(Predicate<I> p) {
        return memoizationMapForPredicates.computeIfAbsent(p, this::memoize);
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

      private <I> Predicate<I> memoize(Predicate<I> predicate) {
        Map<I, BooleanSupplier> memo = new HashMap<>();
        return i -> memo.computeIfAbsent(i,
            k -> {
              Throwable throwable;
              try {
                boolean result = predicate.test(k);
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
        ).getAsBoolean();
      }
    }
  }
}
