package com.github.dakusui.crest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.Op.AND;
import static com.github.dakusui.crest.Crest.Op.OR;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class Crest<I, O> {
  enum Op {
    AND {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new CrestMatchers.AllOf<>(false, matchers);
      }
    },
    OR {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new CrestMatchers.AnyOf<>(false, matchers);
      }
    };

    @SuppressWarnings("unchecked")
    <I, O> Matcher<? super I> create(List<Predicate<? super O>> predicates, Function<? super I, ? extends O> function) {
      return create(
          predicates.stream(
          ).map(
              predicate -> (BaseMatcher<Object>) toMatcher(predicate, function)
          ).collect(
              toList()
          )
      );
    }

    abstract <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers);
  }

  private final Function<? super I, ? extends O> function;
  private final List<Predicate<? super O>>       predicates;
  private Op op = null;

  public static <I, O> Crest<I, O> create(Function<? super I, ? extends O> function) {
    return new Crest<>(function);
  }

  private Crest(Function<? super I, ? extends O> function) {
    this.function = Objects.requireNonNull(function);
    this.predicates = new LinkedList<>();
  }

  @SafeVarargs
  public final Crest<? super I, ? extends O> and(Predicate<? super O>... predicate) {
    requireNull(this.op);
    this.op = AND;
    this.predicates.addAll(asList(predicate));
    return this;
  }

  @SafeVarargs
  public final Crest<? super I, ? extends O> or(Predicate<? super O>... predicate) {
    requireNull(this.op);
    this.op = OR;
    this.predicates.addAll(asList(predicate));
    return this;
  }

  @SuppressWarnings("unchecked")
  public Matcher<? super I> matcher() {
    return predicates.isEmpty() ?
        (Matcher<I>) CoreMatchers.anything() :
        predicates.size() == 1 ?
            toMatcher(predicates.get(0), this.function) :
            Objects.requireNonNull(op).create(predicates, this.function);
  }

  private static <I, O> BaseMatcher<? super I> toMatcher(Predicate<? super O> p, Function<? super I, ? extends O> function) {
    return new BaseMatcher<I>() {
      @SuppressWarnings("unchecked")
      @Override
      public boolean matches(Object item) {
        return p.test(function.apply((I) item));
      }

      @SuppressWarnings("unchecked")
      @Override
      public void describeMismatch(Object item, Description description) {
        description
            .appendDescriptionOf(this).appendText(" ")
            .appendText("was false because " + function.toString() + "(x)=")
            .appendValue(function.apply((I) item))
        ;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(String.format("%s(%s(x))", p.toString(), function.toString()));
      }
    };
  }

  private static void requireNull(Op op) {
    if (op != null)
      throw new IllegalStateException();
  }
}
