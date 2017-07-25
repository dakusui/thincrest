package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.CrestFunctions;
import com.github.dakusui.crest.CrestMatchers;
import com.github.dakusui.crest.CrestPredicates;
import com.github.dakusui.crest.InternalUtils;
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
              predicate -> (BaseMatcher<Object>) InternalUtils.toMatcher(predicate, function)
          ).collect(
              toList()
          )
      );
    }

    abstract <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers);
  }

  @SuppressWarnings("unchecked")
  static <I, C extends Crest<I, I>> C asObject() {
    return (C) asObject(CrestFunctions.identity());
  }

  static <I> AsString<I> asString() {
    return new AsString<>(CrestFunctions.stringify());
  }

  static <E> AsStream<E> asStream() {
    return new AsStream<>(CrestFunctions.stream());
  }

  @SuppressWarnings("unchecked")
  static <I, O, C extends Crest<I, O>> C asObject(String methodName, Object... args) {
    return (C) new AsObject<I, O>(CrestFunctions.invoke(methodName, args));
  }

  @SuppressWarnings("unchecked")
  static <I, O, C extends Crest<I, O>> C asObject(Function<? super I, ? extends O> function) {
    return (C) new AsObject<I, O>((Function<? super I, ? extends O>) function);
  }

  @SuppressWarnings("unchecked")
  <C extends Crest<? super I, ? extends O>> C check(Predicate<? super O> predicate);

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
