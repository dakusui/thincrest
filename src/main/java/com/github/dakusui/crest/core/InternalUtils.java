package com.github.dakusui.crest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.crest.functions.CrestPredicates.isTrue;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum InternalUtils {
  ;

  public static void requireState(boolean stateCondition) {
    if (!stateCondition)
      throw new IllegalStateException();
  }

  public static <I, O> BaseMatcher<? super I> toMatcher(Predicate<? super O> p, Function<? super I, ? extends O> function) {
    return new BaseMatcher<I>() {
      boolean matchesDone = false;
      O value = null;

      @SuppressWarnings("unchecked")
      @Override
      public boolean matches(Object item) {
        try {
          return p.test(this.value = function.apply((I) item));
        } finally {
          matchesDone = true;
        }
      }

      @SuppressWarnings("unchecked")
      @Override
      public void describeMismatch(Object item, Description description) {
        requireState(isTrue(), matchesDone);
        description
            .appendDescriptionOf(this).appendText(" ")
            .appendText(String.format("was false because %s=", formatFunction(function, "x")))
            .appendValue(value)
            .appendText(" does not satisfy it")
        ;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(String.format(
            "%s(%s)",
            p.toString(),
            formatFunction(function, "x")
        ));
      }

      private String formatFunction(Function<?, ?> function, @SuppressWarnings("SameParameterValue") String variableName) {
        return String.format("%s(%s)", function.toString(), variableName);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static boolean areArgsCompatible(Class[] formalParameters, Object[] args) {
    if (formalParameters.length != args.length)
      return false;
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null)
        continue;
      if (!formalParameters[i].isAssignableFrom(args[i].getClass()))
        return false;
    }
    return true;
  }

  public static <T> Optional<T> getIfOnlyOneElseThrow(List<T> in) {
    if (in.size() == 1)
      return Optional.of(in.get(0));
    return Optional.empty();
  }

  public static Method findMethod(Class<?> aClass, String methodName, Object[] args) {
    return getIfOnlyOneElseThrow(
        Arrays.stream(
            aClass.getMethods()
        ).filter(
            (Method m) -> m.getName().equals(methodName)
        ).filter(
            (Method m) -> areArgsCompatible(m.getParameterTypes(), args)
        ).collect(
            toList()
        )
    ).orElseThrow(
        () -> new RuntimeException(String.format(
            "Method matching '%s%s' was not found or more than one were mathing in %s.",
            methodName,
            asList(args),
            aClass.getCanonicalName()
        ))
    );
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(Object target, String methodName, Object[] args) {
    try {
      return (R) findMethod(Objects.requireNonNull(target).getClass(), methodName, args).invoke(target, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static <E extends Throwable, T> T require(Predicate<? super T> condition, T value, Supplier<E> exceptionSupplier) throws E {
    if (!condition.test(value))
      throw exceptionSupplier.get();
    return value;
  }

  public static <T> T requireState(Predicate<T> condition, T value, Supplier<String> messageOnFailure) {
    return require(condition, value, () -> new IllegalStateException(messageOnFailure.get()));
  }

  public static <T> T requireState(Predicate<T> condition, T value, String messageOnFailure) {
    return requireState(condition, value, () -> messageOnFailure);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T requireState(Predicate<T> check, T value) {
    return requireState(check, value, (String) null);
  }
}
