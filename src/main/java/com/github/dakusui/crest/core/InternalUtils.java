package com.github.dakusui.crest.core;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum InternalUtils {
  ;

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

  static String formatExpectation(Predicate p, Function function) {
    return format("%s(%s)", p.toString(), formatFunction(function, "x"));
  }

  static String formatFunction(Function<?, ?> function, @SuppressWarnings("SameParameterValue") String variableName) {
    return format("%s(%s)", function.toString(), variableName);
  }

  /*
     * Based on BaseDescription#appendValue() of Hamcrest
     *
     * http://hamcrest.org/JavaHamcrest/
     */
  static String formatValue(Object value) {
    if (value == null)
      return "null";
    if (value instanceof String)
      return String.format("\"%s\"", toJavaSyntax((String) value));
    if (value instanceof Character)
      return String.format("\"%s\"", toJavaSyntax(((Character) value).charValue()));
    if (value instanceof Short)
      return String.format("<%ss>", value);
    if (value instanceof Long)
      return String.format("<%sL>", value);
    if (value instanceof Float)
      return String.format("<%sF>", value);
    if (value.getClass().isArray())
      return arrayToString(value);
    return format("<%s>", value);
  }

  private static String toJavaSyntax(String unformatted) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < unformatted.length(); i++) {
      b.append(toJavaSyntax(unformatted.charAt(i)));
    }
    return b.toString();
  }

  private static String toJavaSyntax(char ch) {
    switch (ch) {
    case '"':
      return "\\\"";
    case '\n':
      return ("\\n");
    case '\r':
      return ("\\r");
    case '\t':
      return ("\\t");
    default:
      return Character.toString(ch);
    }
  }

  private static String arrayToString(Object arr) {
    StringBuilder b = new StringBuilder();
    b.append("[");
    int length = Array.getLength(arr);
    if (length > 0) {
      for (int i = 0; i < length - 1; i++) {
        b.append(Array.get(arr, i));
        b.append(",");
      }
      b.append(Array.get(arr, length - 1));
    }
    b.append("]");
    return b.toString();
  }
}
