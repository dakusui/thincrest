package com.github.dakusui.crest.matcherbuilders;

import java.util.Map;
import java.util.function.Function;

public class AsMap<I,K,V> extends AsObject<I, Map<K,V>, AsMap<I,K,V>>{
  public AsMap(Function<? super I, ? extends Map<K, V>> function) {
    super(function);
  }
}
