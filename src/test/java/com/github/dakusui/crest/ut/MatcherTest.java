package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.functions.CrestPredicates;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.core.Assertion.assertThat;

public class MatcherTest {
  @Test(expected = ComparisonFailure.class)
  public void givenFailingMatcher$whenAssertThat$thenFail() {
    assertThat(
        "Hello",
        Matcher.Leaf.<Object, Object>create(wrap(CrestPredicates.equalTo("hello")), wrap(CrestFunctions.identity()))
    );
  }

  @Test//(expected = ComparisonFailure.class)
  public void givenFailingNestedMatcher$whenAssertThat$thenFail() {
    assertThat(
        "Hello",
        Matcher.Conjunctive.create(Arrays.<Matcher<Object>>asList(
            Matcher.Leaf.create(wrap(CrestPredicates.equalTo("hello")), wrap(CrestFunctions.identity())),
            Matcher.Leaf.create(wrap(CrestPredicates.equalTo("hello")), wrap(CrestFunctions.identity()))
        )));
  }

  private <T> Predicate<T> wrap(Predicate<T> predicate) {
    AtomicBoolean alreadyCalled = new AtomicBoolean(false);
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        if (alreadyCalled.get())
          throw new IllegalStateException("Already called!");
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return predicate.toString();
      }
    };
  }

  private <T, R> Function<T, R> wrap(Function<T, R> function) {
    AtomicBoolean alreadyCalled = new AtomicBoolean(false);
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        if (alreadyCalled.get())
          throw new IllegalStateException("Already called!");
        return function.apply(t);
      }

      @Override
      public String toString() {
        return function.toString();
      }
    };
  }
}
