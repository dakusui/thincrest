package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.InternalUtils;
import com.github.dakusui.crest.predicates.CrestPredicates;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/*
 * 'S' for 'self'
 */
public interface MatcherBuilder<I, O, S extends MatcherBuilder<I, O, S>> {
  enum Op {
    AND {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new Crest.AllOf<>(false, matchers);
      }
    },
    OR {
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return new Crest.AnyOf<>(false, matchers);
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

  S check(Predicate<? super O> predicate);

  default S check(String methodName, Object... args) {
    return check(CrestPredicates.invoke(methodName, args));
  }

  @SuppressWarnings("unchecked")
  default S equalTo(O value) {
    return (S) this.check(CrestPredicates.equalTo(value));
  }

  Matcher<? super I> all();

  Matcher<? super I> any();

  /**
   * Synonym for {@code all()}.
   *
   * @return A matcher built by this object
   * @see MatcherBuilder#all
   */
  default Matcher<? super I> matcher() {
    return all();
  }
}
