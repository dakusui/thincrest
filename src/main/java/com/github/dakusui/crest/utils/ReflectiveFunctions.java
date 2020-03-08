package com.github.dakusui.crest.utils;

import java.util.function.Function;

import static com.github.dakusui.crest.utils.ReflectionUtils.invokeOn;

public enum ReflectiveFunctions {
  ;

  public static final Object THIS = new Object() {
    public String toString() {
      return "(THIS)";
    }
  };

  public static <I, E> Function<? super I, ? extends E> invoke(String methodName, Object... args) {
    return invokeOn(THIS, methodName, args);
  }
}
