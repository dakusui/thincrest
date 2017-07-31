package com.github.dakusui.crest.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

public interface Matcher<T> {
  boolean matches(T value, Assertion<? extends T> session);

  List<String> describeExpectation(Assertion<? extends T> session);

  List<String> describeMismatch(T value, Assertion<? extends T> session);

  interface Composite<T> extends Matcher<T> {
    List<Matcher<T>> children();

    abstract class Base<T> implements Composite<T> {
      private final List<Matcher<T>> children;

      Base(List<Matcher<T>> children) {
        this.children = Collections.unmodifiableList(requireNonNull(children));
      }

      @Override
      public boolean matches(T value, Assertion<? extends T> session) {
        return false;
      }

      @Override
      public List<String> describeExpectation(Assertion<? extends T> session) {
        return new LinkedList<String>() {{
          add(String.format("%s:[", name()));
          children().forEach(
              eachChild -> add(
                  String.format("  %s", eachChild.describeExpectation(session))
              )
          );
          add("]->true");
        }};
      }

      @Override
      public List<String> describeMismatch(T value, Assertion<? extends T> session) {
        return null;
      }

      @Override
      public List<Matcher<T>> children() {
        return this.children;
      }

      abstract protected String name();
    }
  }

  interface Conjunctive<T> extends Composite<T> {
    static <T> Matcher<T> create(List<? extends Matcher<T>> matchers) {
      return new Conjunctive.Base<T>((List<Matcher<T>>) matchers) {

        @Override
        public boolean matches(T value, Assertion<? extends T> session) {
          return false;
        }

        @Override
        protected String name() {
          return "and";
        }
      };
    }
  }

  interface Disjunctive<T> extends Composite<T> {

  }

  interface Leaf<T> extends Matcher<T> {
    static <I, O> Matcher<? super I> create(Predicate<? super O> p, Function<? super I, ? extends O> function) {
      return new Matcher<I>() {
        @Override
        public boolean matches(I value, Assertion<? extends I> session) {
          return session.test(p, session.apply(function, value));
        }

        @Override
        public List<String> describeExpectation(Assertion<? extends I> session) {
          return singletonList(formatExpectation());
        }

        @Override
        public List<String> describeMismatch(I value, Assertion<? extends I> session) {
          return singletonList(String.format(
              "%s was false because %s=<%s> does not satisfy it",
              formatExpectation(),
              formatFunction(function, "x"),
              session.apply(function, value)
              )
          );
        }

        private String formatExpectation() {
          return format("%s(%s)", p.toString(), formatFunction(function, "x"));
        }

        private String formatFunction(Function<?, ?> function, @SuppressWarnings("SameParameterValue") String variableName) {
          return format("%s(%s)", function.toString(), variableName);
        }
      };
    }
  }
}
