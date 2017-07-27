package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.core.InternalUtils;
import com.github.dakusui.crest.functions.CrestPredicates;
import org.hamcrest.Matcher;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class AsObject<I, O, S extends AsObject<I, O, S>> implements MatcherBuilder<I, O, S> {
  private final Function<? super I, ? extends O> function;
  private final List<Predicate<? super O>>       predicates;

  public AsObject(Function<? super I, ? extends O> function) {
    this.function = function;
    this.predicates = new LinkedList<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public S check(Predicate<? super O> predicate) {
    this.predicates.add(predicate);
    return (S) this;
  }

  @SuppressWarnings("unchecked")
  public S isNull() {
    this.predicates.add(Formattable.predicate("==null", Objects::isNull));
    return (S) this;
  }

  @SuppressWarnings("unchecked")
  public S isNotNull() {
    this.predicates.add(Formattable.predicate("1=null", Objects::isNull));
    return (S) this;
  }

  public S isSameAs(O value) {
    this.predicates.add(CrestPredicates.isSameAs(value));
    return (S) this;
  }

  public S isInstanceOf(Class<?> value) {
    this.predicates.add(CrestPredicates.isInstanceOf(value));
    return (S) this;
  }

  @Override
  public Matcher<? super I> all() {
    return matcher(Op.AND);
  }

  @Override
  public Matcher<? super I> any() {
    return matcher(Op.OR);
  }

  private Matcher<? super I> matcher(Op op) {
    InternalUtils.requireState(!predicates.isEmpty());
    return (predicates.size() == 1) ?
        InternalUtils.toMatcher(predicates.get(0), this.function) :
        Objects.requireNonNull(op).create(predicates, this.function);
  }

}
