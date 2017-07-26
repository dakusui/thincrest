package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.InternalUtils;
import com.github.dakusui.crest.predicates.CrestPredicates;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public interface Crest<I, O> {
  enum Op {
    AND {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new MatcherBuilders.AllOf<>(false, matchers);
      }
    },
    OR {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new MatcherBuilders.AnyOf<>(false, matchers);
      }
    };

    @SuppressWarnings("unchecked")
    <I, O> Matcher<? super I> create(List<Predicate<? super O>> predicates, Function<? super I, ? extends O> function) {
      return create(
          predicates.stream(
          ).map(
              predicate -> (BaseMatcher<Object>) InternalUtils.toMatcher(predicate, function)
          ).collect(
              toList()
          )
      );
    }

    abstract <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers);
  }

  <C extends Crest<? super I, ? extends O>> C check(Predicate<? super O> predicate);

  default <C extends Crest<? super I, ? extends O>> C check(String methodName, Object... args) {
    return check(CrestPredicates.invoke(methodName, args));
  }

  @SuppressWarnings("unchecked")
  default <C extends Crest<? super I, ? extends O>> C equalTo(O value) {
    return (C) this.check(CrestPredicates.equalTo(value));
  }

  Matcher<? super I> all();

  Matcher<? super I> any();

  /**
   * Synonym for {@code all()}.
   *
   * @return A matcher built by this object
   * @see Crest#all
   */
  default Matcher<? super I> matcher() {
    return all();
  }
}
