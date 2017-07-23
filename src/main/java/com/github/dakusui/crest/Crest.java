package com.github.dakusui.crest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class Crest<I, O> {
  private final Function<I, O>     function;
  private final List<Predicate<O>> predicates;

  public Crest(Function<I, O> function) {
    this.function = Objects.requireNonNull(function);
    this.predicates = new LinkedList<>();
  }

  public Crest<I, O> and(Predicate<O> predicate) {
    this.predicates.add(Objects.requireNonNull(predicate));
    return this;
  }

  public Matcher<I> build() {
    //noinspection unchecked
    return CrestUtils.allOf(
        predicates.stream().map(
            p -> new BaseMatcher<I>() {
              @SuppressWarnings("unchecked")
              @Override
              public boolean matches(Object item) {
                return p.test(function.apply((I) item));
              }

              @SuppressWarnings("unchecked")
              @Override
              public void describeMismatch(Object item, Description description) {
                description
                    .appendText("was false because " + function.toString() + "(x)=")
                    .appendValue(function.apply((I) item))
                    .appendText("; x=")
                    .appendValue(item)
                ;
              }

              @Override
              public void describeTo(Description description) {
                description.appendText(String.format("%s(%s(x))", p.toString(), function.toString()));
              }
            }
        ).collect(
            toList()
        ).toArray(new BaseMatcher[predicates.size()])
    );
  }
}
