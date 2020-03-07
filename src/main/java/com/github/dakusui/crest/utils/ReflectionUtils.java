package com.github.dakusui.crest.utils;

import com.github.dakusui.crest.core.TrivialFunction;
import com.github.dakusui.thincrest_pcond.functions.Printable;

import java.util.function.Function;

import static com.github.dakusui.crest.utils.InternalUtils.summarize;
import static com.github.dakusui.crest.utils.ReflectiveFunctions.THIS;

public enum ReflectionUtils {
  ;
  public static <I, E> Function<? super I, ? extends E> invokeOn(Object on, String methodName, Object... args) {
    return Printable.function(
        on == THIS
            ? () -> String.format("%s%s", methodName, summarize(args))
            : () -> String.format("->%s.%s%s", on, methodName, summarize(args)),
        (I target) -> InternalUtils.invokeMethod(
            InternalUtils.replaceTarget(on, target),
            methodName,
            args
        ));
  }

  public static <I, E> Function<? super I, ? extends E> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return Printable.function(
        () -> String.format("->%s.%s%s", klass.getSimpleName(), methodName, summarize(args)),
        (I target) -> InternalUtils.invokeStaticMethod(
            klass,
            target,
            methodName,
            args
        ));
  }

  public static <T, R> TrivialFunction<T, R> trivial(Function<? super T, ? extends R> function) {
    return TrivialFunction.create(function);
  }

}
