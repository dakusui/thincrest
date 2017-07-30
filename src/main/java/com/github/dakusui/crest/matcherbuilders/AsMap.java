package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class AsMap<I, K, V, SELF extends AsMap<I, K, V, SELF>> extends AsObject<I, Map<K, V>, SELF> {
  public AsMap(Function<? super I, ? extends Map<K, V>> function) {
    super(function);
  }

  public AsMap<I, K, V, SELF> hasEntry(K key, V value) {
    return this.check(Formattable.predicate(
        String.format("hasEntry[%s,%s]", key, value),
        map -> map.containsKey(key) && Objects.equals(map.get(key), value)
    ));
  }

  public AsMap<I, K, V, SELF> hasKey(K key) {
    return this.check(Formattable.predicate(
        String.format("hasKey[%s]", key),
        map -> map.containsKey(key)
    ));
  }

  public AsMap<I, K, V, SELF> hasValue(V value) {
    return this.check(Formattable.predicate(
        String.format("hasValue[%s]", value),
        map -> map.containsValue(value)
    ));
  }
}
