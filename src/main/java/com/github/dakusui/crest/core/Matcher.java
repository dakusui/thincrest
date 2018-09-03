package com.github.dakusui.crest.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public interface Matcher<T> {
  boolean matches(T value, Session<T> session, List<Throwable> exceptions);

  void describeExpectation(Session<T> session);

  void describeMismatch(T value, Session<T> session);

  interface Composite<T> extends Matcher<T> {
    default void describeExpectation(Session<T> session) {
      session.describeExpectation(this);
    }

    default void describeMismatch(T value, Session<T> session) {
      session.describeMismatch(value, this);
    }

    boolean isTopLevel();

    List<Matcher<T>> children();

    String name();

    abstract class Base<T> implements Composite<T> {
      private final List<Matcher<T>> children;
      private final boolean          topLevel;

      @SuppressWarnings("unchecked")
      protected Base(boolean topLevel, List<Matcher<? super T>> children) {
        this.children = (List<Matcher<T>>) Collections.<T>unmodifiableList((List<? extends T>) requireNonNull(children));
        this.topLevel = topLevel;
      }

      @Override
      public boolean matches(T value, Session<T> session, List<Throwable> exceptions) {
        List<Throwable> work = new LinkedList<>();
        boolean ret = first();
        for (Matcher<T> eachChild : children())
          ret = op(ret, eachChild.matches(value, session, work));
        exceptions.addAll(work);
        return ret && work.isEmpty();
      }

      @Override
      public boolean isTopLevel() {
        return this.topLevel;
      }

      @Override
      public List<Matcher<T>> children() {
        return this.children;
      }

      abstract protected boolean first();

      abstract protected boolean op(boolean current, boolean next);

    }
  }

  interface Conjunctive<T> extends Composite<T> {
    @SuppressWarnings("unchecked")
    static <T> Matcher<T> create(boolean topLevel, List<Matcher<? super T>> matchers) {
      return new Conjunctive.Base<T>(topLevel, matchers) {
        @Override
        public String name() {
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
        public String name() {
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
        public String name() {
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
    default void describeExpectation(Session<T> session) {
      session.describeExpectation(this);
    }

    default void describeMismatch(T value, Session<T> session) {
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

        @Override
        public boolean matches(I value, Session<I> session, List<Throwable> exceptions) {
          try {
            return session.matches(this, value, exceptions::add) && exceptions.isEmpty();
          } catch (Throwable e) {
            return false;
          }
        }
      };
    }
  }
}