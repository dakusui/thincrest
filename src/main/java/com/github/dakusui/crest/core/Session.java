package com.github.dakusui.crest.core;

import com.github.dakusui.crest.functions.TransformingPredicate;

import java.util.*;
import java.util.function.*;

import static com.github.dakusui.crest.core.InternalUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public interface Session<T> {
  static <T> void perform(String message, T value, Matcher<? super T> matcher, ExceptionFactory exceptionFactory) {
    Report report = perform(value, matcher);
    if (!report.isSuccessful()) {
      if (report.exceptions().isEmpty()) {
        Throwable exception = exceptionFactory.create(message, report, report.exceptions());
        if (exception instanceof RuntimeException)
          throw (RuntimeException) exception;
        if (exception instanceof Error)
          throw (Error) exception;
        throw new RuntimeException(exception);
      }
      throw new ExecutionFailure(message, report.expectation(), report.mismatch(), report.exceptions());
    }
  }

  static <T> Report perform(T value, Matcher<T> matcher) {
    return perform(value, matcher, create());
  }

  static <T> Report perform(T value, Matcher<T> matcher, Session<T> session) {
    if (matcher.matches(value, session, new LinkedList<>())) {
      session.matched(true);
    } else {
      matcher.describeExpectation(session.matched(false));
      matcher.describeMismatch(value, session);
    }
    return session.report();
  }

  Report report();

  void describeExpectation(Matcher.Composite<T> matcher);

  void describeExpectation(Matcher.Leaf<T> matcher);

  void describeMismatch(T value, Matcher.Composite<T> matcher);

  void describeMismatch(T value, Matcher.Leaf<T> matcher);

  @SuppressWarnings("unchecked")
  default <X> boolean matches(Matcher.Leaf<T> leaf, T value, Consumer<Throwable> listener) {
    try {
      return this.test(
          (Predicate<X>) leaf.p(),
          this.apply(
              (Function<T, X>) leaf.func(),
              value
          ));
    } catch (RuntimeException | Error exception) {
      listener.accept(exception);
      addException(exception);
      return false;
    }
  }

  <I, O> O apply(Function<I, O> func, I value);

  <I> boolean test(Predicate<I> pred, I value);

  static <T> Session<T> create() {
    return new Impl<>();
  }

  Session<T> addException(Throwable exception);

  Session<T> matched(boolean b);

  @FunctionalInterface
  interface ExceptionFactory {
    Throwable create(String message, Report report, List<Throwable> causes);
  }

  class Impl<T> implements Session<T> {
    private HashSet<List<Object>> explained = new HashSet<>();

    class Writer {
      private int          level  = 0;
      private List<String> buffer = new LinkedList<>();

      Impl.Writer enter() {
        level++;
        return this;
      }

      Impl.Writer leave() {
        level--;
        return this;
      }

      Impl.Writer appendLine(String format, Object... args) {
        buffer.add(indent(this.level) + format(format, args));
        return this;
      }

      String write() {
        return String.join("\n", this.buffer);
      }

      private String indent(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
          builder.append("  ");
        }
        return builder.toString();
      }
    }

    private static final String VARIABLE_NAME             = "x";
    private static final String TRANSFORMED_VARIABLE_NAME = "y";
    Map<Function, Function>   memoizationMapForFunctions  = new HashMap<>();
    Map<Predicate, Predicate> memoizationMapForPredicates = new HashMap<>();

    Impl.Writer expectationWriter = new Impl.Writer();
    Impl.Writer mismatchWriter    = new Impl.Writer();

    private boolean         result;
    private List<Throwable> exceptions = new LinkedList<>();

    @Override
    public Report report() {
      if (!Impl.this.exceptions.isEmpty())
        mismatchWriter.appendLine("FAILED");
      return new Report() {
        private List<Throwable> exceptions = Collections.unmodifiableList(Impl.this.exceptions);
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
          return this.exceptions;
        }

        @Override
        public boolean isSuccessful() {
          return result && exceptions().isEmpty();
        }
      };

    }

    @Override
    public void describeExpectation(Matcher.Composite<T> matcher) {
      beginExpectation(matcher);
      try {
        matcher.children().forEach(each -> each.describeExpectation(this));
      } finally {
        endExpectation(matcher);
      }
    }

    void beginExpectation(Matcher.Composite<T> matcher) {
      expectationWriter.appendLine(format("%s:[", matcher.name())).enter();
    }

    @SuppressWarnings("unused")
    void endExpectation(Matcher.Composite<T> matcher) {
      expectationWriter.leave().appendLine("]");
    }

    @Override
    public void describeMismatch(T value, Matcher.Composite<T> matcher) {
      beginMismatch(value, matcher);
      try {
        matcher.children().forEach(each -> each.describeMismatch(value, this));
      } finally {
        endMismatch(value, matcher);
      }
    }

    @Override
    public void describeExpectation(Matcher.Leaf<T> matcher) {
      describeExpectationTo(expectationWriter, matcher);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void describeMismatch(T value, Matcher.Leaf<T> matcher) {
      if (this.matches(matcher, value, NOP)) {
        describeExpectationTo(mismatchWriter, matcher);
        return;
      }
      Function<T, ?> func = matcher.func();
      Predicate<?> p = matcher.p();
      String formattedExpectation = formatExpectation(p, func);
      String formattedFunction = formatFunction(func, VARIABLE_NAME);
      String formattedFunctionOutput = getAndFormatValue(() -> this.apply(func, value));
      if (fails(func, value)) {
        this.mismatchWriter.appendLine(
            "%s failed with %s",
            formattedExpectation,
            formattedFunctionOutput
        );
      } else if (fails(p, value)) {
        this.mismatchWriter.appendLine(
            "%s failed with %s",
            formattedExpectation,
            getAndFormatValue(() -> this.test((Predicate) p, this.apply(func, value)))
        );
      } else {
        this.mismatchWriter.appendLine(
            "%s was not met because %s=%s",
            formattedExpectation,
            formattedFunction,
            formattedFunctionOutput
        );
      }
      if (func instanceof Call.ChainedFunction) {
        this.mismatchWriter
            .enter()
            .appendLine("%s=%s(%s)", TRANSFORMED_VARIABLE_NAME, func, VARIABLE_NAME);
        try {
          explainFunction(value, func, VARIABLE_NAME, this.mismatchWriter);
        } finally {
          this.mismatchWriter.leave();
        }
      }
      if (p instanceof TransformingPredicate)
        explainFunction((T) apply(func, value), ((TransformingPredicate) p).function(), TRANSFORMED_VARIABLE_NAME, this.mismatchWriter);
    }

    void describeExpectationTo(Impl.Writer writer, Matcher.Leaf<T> matcher) {
      writer.appendLine(formatExpectation(matcher.p(), matcher.func()));
    }

    void beginMismatch(T value, Matcher.Composite<T> matcher) {
      if (matcher.isTopLevel())
        this.mismatchWriter.appendLine(format("when %s=%s; then %s:[", VARIABLE_NAME, formatValue(value), matcher.name()));
      else
        this.mismatchWriter.appendLine(format("%s:[", matcher.name()));
      mismatchWriter.enter();
    }

    void endMismatch(T value, Matcher.Composite<T> matcher) {
      this.mismatchWriter.leave().appendLine(format("]->%s", matcher.matches(value, this, new LinkedList<>())));
    }


    @Override
    public Session<T> addException(Throwable exception) {
      this.exceptions.add(exception);
      return this;
    }

    @SuppressWarnings("unchecked")
    private boolean fails(Function func, Object value) {
      try {
        this.apply(func, value);
        return false;
      } catch (Throwable t) {
        return true;
      }
    }

    @SuppressWarnings("unchecked")
    private boolean fails(Predicate p, Object value) {
      try {
        this.test(p, value);
        return false;
      } catch (Throwable t) {
        return true;
      }
    }

    /**
     * During the assertion process invoked by {@code perform} method, {@code apply}
     * method of {@code Function}s held by the matcher to be performed must not
     * be called directly but through this method.
     *
     * @param func  A function to be applied.
     * @param value A value given to {@code func}.
     * @param <I>   A type of {@code value} given to {@code func}.
     * @param <O>   A type of {@code value} returned from {@code func}
     * @return Result of {@code func} with {@code value}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O apply(Function<I, O> func, I value) {
      if (func instanceof Call.ChainedFunction) {
        Call.ChainedFunction cf = (Call.ChainedFunction) func;
        if (cf.previous() != null)
          return (O) apply(cf.chained(), apply(cf.previous(), value));
      }
      return memoizedFunction(func).apply(value);
    }

    /**
     * During the assertion process invoked by {@code perform} method, {@code test}
     * method of {@code Predicate}s held by the matcher to be performed must not
     * be called directly but through this method.
     *
     * @param pred  A predicate to be applied.
     * @param value A value given to {@code pred}.
     * @param <I>   A type of {@code value} given to {@code pred}.
     * @return Result of {@code pred} with {@code value}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <I> boolean test(Predicate<I> pred, I value) {
      if (pred instanceof TransformingPredicate) {
        Function func = ((TransformingPredicate) pred).function();
        return test(((TransformingPredicate) pred).predicate(), apply(func, value));
      }
      return memoizedPredicate(pred).test(value);
    }

    @Override
    public Session<T> matched(boolean b) {
      this.result = b;
      return this;
    }

    @SuppressWarnings("unchecked")
    private <I, O> Function<I, O> memoizedFunction(Function<I, O> function) {
      return memoizationMapForFunctions.computeIfAbsent(function, Impl::memoize);
    }

    @SuppressWarnings("unchecked")
    private <I> Predicate<I> memoizedPredicate(Predicate<I> p) {
      return memoizationMapForPredicates.computeIfAbsent(p, Impl::memoize);
    }

    private static <I, O> Function<I, O> memoize(Function<I, O> function) {
      Map<I, Supplier<O>> memo = new HashMap<>();
      return i -> memo.computeIfAbsent(i,
          k -> {
            try {
              O result = function.apply(k);
              return () -> result;
            } catch (RuntimeException | Error e) {
              return () -> {
                if (e instanceof RuntimeException)
                  throw (RuntimeException) e;
                throw (Error) e;
              };
            }
          }
      ).get();
    }

    private static <I> Predicate<I> memoize(Predicate<I> predicate) {
      Map<I, BooleanSupplier> memo = new HashMap<>();
      return i -> memo.computeIfAbsent(i,
          k -> {
            try {
              boolean result = predicate.test(k);
              return () -> result;
            } catch (RuntimeException | Error e) {
              String snapshot = InternalUtils.formatValue(e);
              return new BooleanSupplier() {
                @Override
                public boolean getAsBoolean() {
                  if (e instanceof RuntimeException)
                    throw (RuntimeException) e;
                  throw (Error) e;
                }

                @Override
                public String toString() {
                  return snapshot;
                }
              };
            }
          }
      ).getAsBoolean();
    }

    private boolean isAlreadyExplained(T value, Function<T, ?> func, String variableName) {
      return this.explained.contains(asList(value, func, variableName));
    }

    private void explained(T value, Function<T, ?> func, String variableName) {
      this.explained.add(asList(value, func, variableName));
    }

    @SuppressWarnings("unchecked")
    private void explainFunction(T value, Function<T, ?> func, String variableName, Impl.Writer writer) {
      if (isAlreadyExplained(value, func, variableName)) {
        writer.enter().appendLine("%s(%s)=(EXPLAINED)", func, variableName).leave();
        return;
      }
      if (func instanceof Call.ChainedFunction) {
        explainChainedFunction(value, (Call.ChainedFunction) func, variableName, writer);
      } else {
        writer.enter();
        try {
          writer.appendLine("%s(%s)=%s", func, variableName, getAndFormatValue(() -> this.apply(func, value)));
        } finally {
          writer.leave();
        }
      }
      explained(value, func, variableName);
    }

    @SuppressWarnings("unchecked")
    private <I, O> void explainChainedFunction(I value, Call.ChainedFunction<I, O> chained, String variableName, Impl.Writer writer) {
      writer.enter();
      try {
        List<String> buffer = new LinkedList<>();
        for (Call.ChainedFunction c = chained; c != null; c = c.previous()) {
          Call.ChainedFunction finalC = c;
          buffer.add(format("%s=%s",
              formatFunction(finalC, variableName),
              getAndFormatValue(() -> this.apply(finalC, value))
          ));
        }
        Collections.reverse(buffer);
        buffer.forEach(each -> writer.appendLine(each));
      } finally {
        writer.leave();
      }
    }

    private static String formatExpectation(Predicate p, Function function) {
      if (p instanceof TransformingPredicate) {
        TransformingPredicate pp = (TransformingPredicate) p;
        return format("%s%s %s",
            pp.name().isPresent() ?
                pp.name().get() + ", i.e. " :
                "",
            formatFunction(pp.function(), formatFunction(function, "x")), pp.predicate());
      } else
        return format("%s %s", formatFunction(function, "x"), p.toString());
    }
  }
}
