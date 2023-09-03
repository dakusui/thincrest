package com.github.dakusui.thincrest.metamor;

import com.github.dakusui.thincrest_pcond.core.Evaluable;
import com.github.dakusui.thincrest_pcond.core.printable.PrintablePredicate;

import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public class PropositionPredicate extends PrintablePredicate<Proposition> implements Evaluable.LeafPred<Proposition> {
  public static final PropositionPredicate INSTANCE = new PropositionPredicate();
  
  protected PropositionPredicate() {
    super(new Object(), emptyList(), () -> "evaluate", Proposition::evaluate);
  }
  
  @Override
  public Predicate<? super Proposition> predicate() {
    return super.predicate;
  }
}
