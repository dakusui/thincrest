package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class AsMap<I, K, V> extends AsObject<I, Map<K, V>, AsMap<I, K, V>> {
  public AsMap(Function<? super I, ? extends Map<K, V>> function) {
    super(function);
  }

  public AsMap<I, K, V> hasEntry(K key, V value) {
    return this.check(Formattable.predicate(
        "containsKey",
        map -> map.containsKey(key) && Objects.equals(map.get(key), value)
    ));
  }

  public AsMap<I, K, V> hasKey(K key) {
    return this.check(Formattable.predicate(
        "hasKey",
        map -> map.containsKey(key)
    ));
  }

  public AsMap<I, K, V> hasValue(V value) {
    return this.check(Formattable.predicate(
        "hasValue",
        map -> map.containsValue(value)
    ));
  }
}
