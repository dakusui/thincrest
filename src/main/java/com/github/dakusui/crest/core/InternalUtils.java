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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum InternalUtils {
  ;

  public static <I, O> BaseMatcher<? super I> createLeafMatcher(Predicate<? super O> p, Function<? super I, ? extends O> function) {
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
        if (formalParameters[i].isPrimitive())
          return false;
        else
          continue;
      if (!formalParameters[i].isAssignableFrom(toPrimitiveIfWrapper(args[i].getClass())))
        return false;
    }
    return true;
  }

  private static Class<?> toPrimitiveIfWrapper(Class<?> in) {
    for (Class<?>[] pair : new Class<?>[][] {
        { boolean.class, Boolean.class },
        { byte.class, Byte.class },
        { char.class, Character.class },
        { short.class, Short.class },
        { int.class, Integer.class },
        { long.class, Long.class },
        { float.class, Float.class },
        { double.class, Double.class },
    }) {
      if (Objects.equals(in, pair[1]))
        return pair[0];
    }
    return in;
  }


  private static <T> Optional<T> getIfOnlyOneElseThrow(List<T> foundMethods, Class<?> aClass, String methodName, Object[] args) {
    if (foundMethods.isEmpty())
      return Optional.empty();
    if (foundMethods.size() == 1)
      return Optional.of(foundMethods.get(0));
    throw new RuntimeException(String.format(
        "Methods matching '%s%s' were found more than one in %s.: %s",
        methodName,
        asList(args),
        aClass.getCanonicalName(),
        foundMethods
    ));
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
        ),
        aClass,
        methodName,
        args
    ).orElseThrow(
        () -> new RuntimeException(String.format(
            "Method matching '%s%s' was not found in %s.(CAUTION: This method doesn't try to cast or unbox arguments to find a method)",
            methodName,
            Arrays.asList(args),
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

}
