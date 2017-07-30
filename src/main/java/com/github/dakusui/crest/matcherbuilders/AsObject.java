package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.InternalUtils;
import com.github.dakusui.crest.core.Precondition;
import com.github.dakusui.crest.functions.CrestPredicates;
import com.github.dakusui.crest.functions.TransformingPredicate;
import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class AsObject<IN, OUT, SELF extends AsObject<IN, OUT, SELF>> implements MatcherBuilder<IN, OUT, SELF> {
  private final Function<? super IN, ? extends OUT> function;
  private final List<Predicate<? super OUT>>        predicates;

  public AsObject(Function<? super IN, ? extends OUT> function) {
    this.function = function;
    this.predicates = new LinkedList<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SELF check(Predicate<? super OUT> predicate) {
    this.predicates.add(predicate);
    return (SELF) this;
  }

  @Override
  public <P> SELF check(Function<? super OUT, ? extends P> function, Predicate<? super P> predicate) {
    return this.check(new TransformingPredicate<P, OUT>(predicate, function));
  }

  public SELF isNull() {
    return this.check(CrestPredicates.isNull());
  }

  public SELF isNotNull() {
    return this.check(CrestPredicates.isNotNull());
  }

  public SELF isSameAs(OUT value) {
    return this.check(CrestPredicates.isSameAs(value));
  }

  public SELF isInstanceOf(Class<?> value) {
    return this.check(CrestPredicates.isInstanceOf(value));
  }

  @Override
  public Matcher<? super IN> all() {
    return matcher(Op.AND);
  }

  @Override
  public Matcher<? super IN> any() {
    return matcher(Op.OR);
  }

  private Matcher<? super IN> matcher(Op op) {
    Precondition.requireState(!predicates.isEmpty());
    return (predicates.size() == 1) ?
        InternalUtils.toMatcher(predicates.get(0), this.function) :
        Objects.requireNonNull(op).create(predicates, this.function);
  }
}
