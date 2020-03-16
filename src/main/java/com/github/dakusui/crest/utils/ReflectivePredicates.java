package com.github.dakusui.crest.utils;

import com.github.dakusui.thincrest_pcond.functions.Printables;

import java.util.function.Predicate;

import static com.github.dakusui.crest.utils.InternalUtils.summarize;

public class ReflectivePredicates {
  ;
  public static <T> Predicate<? super T> invoke(String methodName, Object... args) {
    return Printables.predicate(
        () -> String.format(".%s%s", methodName, String.join(",", summarize(args))),
        (Object target) -> InternalUtils.invokeMethod(target, methodName, args)
    );
  }

}
