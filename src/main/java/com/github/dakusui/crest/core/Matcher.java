package com.github.dakusui.crest.core;

import com.github.dakusui.crest.functions.TransformingPredicate;

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

  interface Composite<T> extends Matcher<T> {
    abstract class Base<T> implements Composite<T> {
      private final List<Matcher<T>> children;
      private final boolean          topLevel;

      @SuppressWarnings("unchecked")
      Base(boolean topLevel, List<Matcher<? super T>> children) {
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

      List<Matcher<T>> children() {
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
    static <I, O> Matcher<I> create(Predicate<? super O> p, Function<? super I, ? extends O> function) {
      return new Matcher<I>() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(I value, Assertion<? extends I> session) {
          return session.test((Predicate<Object>) p, session.apply(function, value));
        }

        @Override
        public List<String> describeExpectation(Assertion<? extends I> session) {
          return singletonList(formatExpectation(p, function));
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<String> describeMismatch(I value, Assertion<? extends I> session) {
          @SuppressWarnings("unchecked") Optional<Throwable> exception = session.thrownExceptionFor((Predicate<? super I>) p, (I) session.apply(function, value));
          return exception.map(throwable -> singletonList(String.format(
              "%s failed with %s(%s)",
              formatExpectation(p, function),
              throwable.getClass().getCanonicalName(),
              throwable.getMessage()
          ))).orElseGet(() -> {
            if (p instanceof TransformingPredicate) {
              TransformingPredicate pp = (TransformingPredicate) p;
              return singletonList(String.format(
                  "%s%s was false because %s=%s; %s=%s",
                  formatExpectation(p, function),
                  pp.name().isPresent() ? "," : "",
                  InternalUtils.formatFunction(pp.function(), InternalUtils.formatFunction(function, "x")),
                  InternalUtils.formatValue(session.apply(pp.function(), session.apply(function, value))),
                  InternalUtils.formatFunction(function, "x"),
                  InternalUtils.formatValue(session.apply(function, value))
              ));
            }
            return singletonList(String.format(
                "%s was false because %s=%s",
                formatExpectation(p, function),
                InternalUtils.formatFunction(function, "x"),
                InternalUtils.formatValue(session.apply(function, value))));
          });
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