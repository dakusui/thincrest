package com.github.dakusui.crest.core;

import com.github.dakusui.crest.utils.printable.Functions;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface Call {
  Call andThen(String methodName, Object... args);

  <T> Function<Object, T> $();

  static Call create(String methodName, Object... args) {
    return new Call.Impl(null, methodName, args);
  }

  class Impl implements Call {
    private final String   methodName;
    private final Object[] args;
    private final Call     parent;

    Impl(Call parent, String methodName, Object... args) {
      this.parent = parent;
      this.methodName = requireNonNull(methodName);
      this.args = args;
    }

    @Override
    public Call andThen(String methodName, Object... args) {
      return new Impl(this, methodName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Function<Object, T> $() {
      return this.parent == null ?
          Function.class.cast(Functions.invoke(methodName, args)) :
          this.parent.$().andThen(Function.class.cast(Functions.invoke(methodName, args)));
    }
  }
}
