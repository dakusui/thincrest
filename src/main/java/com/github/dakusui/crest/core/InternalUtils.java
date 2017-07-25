package com.github.dakusui.crest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.function.Function;
import java.util.function.Predicate;

public enum InternalUtils {
  ;

  public static void requireState(boolean stateCondition) {
    if (!stateCondition)
      throw new IllegalStateException();
  }

  public static <I, O> BaseMatcher<? super I> toMatcher(Predicate<? super O> p, Function<? super I, ? extends O> function) {
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
}
