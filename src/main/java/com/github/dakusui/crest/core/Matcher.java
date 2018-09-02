package com.github.dakusui.crest.core;

import com.github.dakusui.crest.functions.TransformingPredicate;
import com.github.dakusui.crest.utils.printable.PrintableFunction;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Matcher<T> {
  boolean matches(T value, Assertion<? extends T> session);

  List<String> describeExpectation(Assertion<? extends T> session);

  List<String> describeMismatch(T value, Assertion<? extends T> session);

  boolean matches(T value, Report.Session<T> session);

  void describeExpectation(Report.Session<T> session);

  void describeMismatch(T value, Report.Session<T> session);

  interface Composite<T> extends Matcher<T> {
    default void describeExpectation(Report.Session<T> session) {
      session.describeExpectation(this);
    }

    default void describeMismatch(T value, Report.Session<T> session) {
      session.describeMismatch(value, this);
    }

    List<Matcher<T>> children();

    abstract class Base<T> implements Composite<T> {
      private final List<Matcher<T>> children;
      private final boolean          topLevel;

      @SuppressWarnings("unchecked")
      public Base(boolean topLevel, List<Matcher<? super T>> children) {
        this.children = (List<Matcher<T>>) Collections.<T>unmodifiableList((List<? extends T>) requireNonNull(children));
        this.topLevel = topLevel;
      }

      @Override
      public boolean matches(T value, Assertion<? extends T> session) {
        boolean ret = first();
        for (Matcher<T> eachChild : children())
          ret = op(ret, eachChild.matches(value, session));
        return ret && session.exceptions().isEmpty();
      }


      @Override
      public boolean matches(T value, Report.Session<T> session) {
        boolean ret = first();
        for (Matcher<T> eachChild : children())
          ret = op(ret, eachChild.matches(value, session));
        return ret;
      }

      @Override
      public List<String> describeExpectation(Assertion<? extends T> session) {
        return new LinkedList<String>() {{
          add(String.format("%s:[", name()));
          children().forEach(
              (Matcher<T> eachChild) -> {
                List<String> formattedExpectation = eachChild.describeExpectation(session);
                if (formattedExpectation.size() == 1)
                  add(String.format("  %s", formattedExpectation.get(0)));
                else {
                  addAll(indent(formattedExpectation));
                }
              }
          );
          add("]");
        }};
      }

      @Override
      public List<String> describeMismatch(T value, Assertion<? extends T> session) {
        return new LinkedList<String>() {{
          if (topLevel)
            add(String.format("when x=%s; then %s:[", InternalUtils.formatValue(value), name()));
          else
            add(String.format("%s:[", name()));
          for (Matcher<T> eachChild : children()) {
            if (eachChild.matches(value, session))
              addAll(indent(eachChild.describeExpectation(session)));
            else
              addAll(indent(eachChild.describeMismatch(value, session)));
          }
          add(String.format("]->%s", matches(value, session)));
          session.exceptions().forEach(
              e -> {
                add(e.getMessage());
                addAll(
                    indent(Arrays.stream(e.getStackTrace()).map(
                        StackTraceElement::toString
                    ).collect(toList())));
              }
          );
        }};
      }

      List<String> indent(List<String> in) {
        return in.stream().map(s -> "  " + s).collect(toList());
      }

      @Override
      public List<Matcher<T>> children() {
        return this.children;
      }

      abstract protected String name();

      abstract protected boolean first();

      abstract protected boolean op(boolean current, boolean next);
    }
  }

  interface Conjunctive<T> extends Composite<T> {
    @SuppressWarnings("unchecked")
    static <T> Matcher<T> create(boolean topLevel, List<Matcher<? super T>> matchers) {
      return new Conjunctive.Base<T>(topLevel, matchers) {
        @Override
        protected String name() {
          return "and";
        }

        @Override
        protected boolean first() {
          return true;
        }

        @Override
        protected boolean op(boolean current, boolean next) {
          return current && next;
        }
      };
    }
  }

  interface Disjunctive<T> extends Composite<T> {
    @SuppressWarnings("unchecked")
    static <T> Matcher<T> create(boolean topLevel, List<Matcher<? super T>> matchers) {
      return new Composite.Base<T>(topLevel, matchers) {

        @Override
        protected String name() {
          return "or";
        }

        @Override
        protected boolean first() {
          return false;
        }

        @Override
        protected boolean op(boolean current, boolean next) {
          return current || next;
        }
      };
    }
  }

  interface Negative<T> extends Composite<T> {
    static <T> Matcher<T> create(Matcher<? super T> matcher) {
      return new Composite.Base<T>(true, Collections.singletonList(matcher)) {
        @Override
        protected String name() {
          return "not";
        }

        @Override
        protected boolean first() {
          return true;
        }

        @Override
        protected boolean op(boolean current, boolean next) {
          return current && !next;
        }
      };
    }
  }

  interface Leaf<T> extends Matcher<T> {
    default void describeExpectation(Report.Session<T> session) {
      session.describeExpectation(this);
    }

    default void describeMismatch(T value, Report.Session<T> session) {
      session.describeMismatch(value, this);
    }

    Predicate<?> p();

    Function<T, ?> func();

    static <I, O> Leaf<I> create(Predicate<? super O> p, Function<? super I, ? extends O> function) {
      return new Leaf<I>() {
        @Override
        public Predicate<?> p() {
          return p;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Function<I, ?> func() {
          return (Function<I, ?>) function;
        }

        @SuppressWarnings({ "unchecked", "SimplifiableConditionalExpression" })
        @Override
        public boolean matches(I value, Assertion<? extends I> session) {
          return session.thrownExceptionFor(function, value).isPresent()
              ? false
              : session.test((Predicate<Object>) p, session.apply(function, value));
        }

        @Override
        public boolean matches(I value, Report.Session<I> session) {
          return session.matches(this, value);
        }

        @Override
        public List<String> describeExpectation(Assertion<? extends I> session) {
          return singletonList(formatExpectation(p, function));
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<String> describeMismatch(I value, Assertion<? extends I> session) {
          @SuppressWarnings("unchecked") Optional<Throwable> exception = session.thrownExceptionFor(function, value);
          exception = exception.isPresent()
              ? exception
              : session.thrownExceptionFor((Predicate<? super I>) p, (I) session.apply(function, value));
          List<String> ret = new LinkedList<>();
          renderFunction(value, session, ret, function);
          return exception.map(throwable -> singletonList(String.format(
              "%s failed with %s(%s)",
              formatExpectation(p, function),
              throwable.getClass().getCanonicalName(),
              throwable.getMessage()
          ))).orElseGet(() -> {
            if (p instanceof TransformingPredicate) {
              TransformingPredicate pp = (TransformingPredicate) p;
              Function f = ((TransformingPredicate) p).function();
              ret.add(String.format(
                  "%s%s was not met because %s=%s",
                  formatExpectation(p, function),
                  pp.name().isPresent() ? "," : "",
                  InternalUtils.formatFunction(f, InternalUtils.formatFunction(function, "x")),
                  InternalUtils.formatValue(session.apply(f, value))
              ));
              renderFunction(value, session, ret, f);
              return ret;
            }
            return singletonList(String.format(
                "%s was not met because %s=%s",
                formatExpectation(p, function),
                InternalUtils.formatFunction(function, "x"),
                InternalUtils.formatValue(session.apply(function, value))));
          });
        }

        private void renderFunction(I value, Assertion<? extends I> session, List<String> ret, Function<? super I, ? extends O> function) {
          if (function instanceof Call.ChainedFunction) {
            Call.ChainedFunction c = (Call.ChainedFunction) function;
            renderChainedFunction(value, session, ret, c);
            return;
          }
          if (function instanceof PrintableFunction) {
            ret.add(String.format("  %s=%s",
                InternalUtils.formatFunction(function, "x"),
                function.apply(value)
            ));
          }
        }

        private void renderChainedFunction(I value, Assertion<? extends I> session, List<String> ret, Call.ChainedFunction c) {
          while ((c = c.previous()) != null) {
            ret.add(String.format("  %s=%s",
                InternalUtils.formatFunction(c, "LEFT"),
                InternalUtils.formatValue(session.apply(c, value))
            ));
          }
        }

        String formatExpectation(Predicate p, Function function) {
          if (p instanceof TransformingPredicate) {
            TransformingPredicate pp = (TransformingPredicate) p;
            return String.format("%s%s %s",
                pp.name().isPresent() ?
                    pp.name().get() + ", i.e. " :
                    "",
                InternalUtils.formatFunction(pp.function(), InternalUtils.formatFunction(function, "x")), pp.predicate());
          } else
            return String.format("%s %s", InternalUtils.formatFunction(function, "x"), p.toString());
        }
      };
    }
  }
}