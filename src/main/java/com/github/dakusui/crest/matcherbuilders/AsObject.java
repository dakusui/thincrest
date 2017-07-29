package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;
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

  @SuppressWarnings("unchecked")
  @Override
  public <P> SELF check(Function<? super OUT, ? extends P> function, Predicate<? super P> predicate) {
    this.predicates.add(new TransformingPredicate<P, OUT>(predicate, function));
    return (SELF) this;
  }

  @SuppressWarnings("unchecked")
  public SELF isNull() {
    this.predicates.add(Formattable.predicate("==null", Objects::isNull));
    return (SELF) this;
  }

  @SuppressWarnings("unchecked")
  public SELF isNotNull() {
    this.predicates.add(Formattable.predicate("1=null", Objects::isNull));
    return (SELF) this;
  }

  @SuppressWarnings("unchecked")
  public SELF isSameAs(OUT value) {
    this.predicates.add(CrestPredicates.isSameAs(value));
    return (SELF) this;
  }

  @SuppressWarnings("unchecked")
  public SELF isInstanceOf(Class<?> value) {
    this.predicates.add(CrestPredicates.isInstanceOf(value));
    return (SELF) this;
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
    InternalUtils.requireState(!predicates.isEmpty());
    return (predicates.size() == 1) ?
        InternalUtils.toMatcher(predicates.get(0), this.function) :
        Objects.requireNonNull(op).create(predicates, this.function);
  }

}
