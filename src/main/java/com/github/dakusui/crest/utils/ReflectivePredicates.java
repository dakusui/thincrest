package com.github.dakusui.crest.utils;

import com.github.dakusui.thincrest_pcond.functions.Printable;

import java.util.function.Predicate;

import static com.github.dakusui.crest.utils.InternalUtils.summarize;

public class ReflectivePredicates {
  ;
  public static <T> Predicate<? super T> invoke(String methodName, Object... args) {
    return Printable.predicate(
        () -> String.format(".%s%s", methodName, String.join(",", summarize(args))),
        (Object target) -> InternalUtils.invokeMethod(target, methodName, args)
    );
  }

}
