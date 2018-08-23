package com.github.dakusui.crest.core;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;

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
            getMethods(aClass)
        ).filter(
            (Method m) -> m.getName().equals(methodName)
        ).filter(
            (Method m) -> areArgsCompatible(m.getParameterTypes(), args)
        ).collect(
            LinkedList::new,
            InternalUtils::addMethodIfNecessary,
            (List<Method> methods, List<Method> methods2) -> methods2.forEach(
                method -> {
                  addMethodIfNecessary(methods, method);
                }
            )
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

  private static void addMethodIfNecessary(List<Method> methods, Method method) {
    Optional<Method> found = methods.stream().filter(
        each -> Arrays.equals(each.getParameterTypes(), method.getParameterTypes())
    ).findAny();
    if (found.isPresent()) {
      if (found.get().getDeclaringClass().isAssignableFrom(method.getDeclaringClass()))
        methods.remove(found.get());
    }
    methods.add(method);
  }

  private static Method[] getMethods(Class<?> aClass) {
    return aClass.getMethods();
  }

  static String formatFunction(Function<?, ?> function, @SuppressWarnings("SameParameterValue") String variableName) {
    return format("%s(%s)", function.toString(), variableName);
  }

  /*
   * Based on BaseDescription#appendValue() of Hamcrest
   *
   * http://hamcrest.org/JavaHamcrest/
   */
  public static String formatValue(Object value) {
    if (value == null)
      return "null";
    if (value instanceof String)
      return String.format("\"%s\"", toJavaSyntax((String) value));
    if (value instanceof Character)
      return String.format("\"%s\"", toJavaSyntax((Character) value));
    if (value instanceof Short)
      return String.format("<%ss>", value);
    if (value instanceof Long)
      return String.format("<%sL>", value);
    if (value instanceof Float)
      return String.format("<%sF>", value);
    if (value.getClass().isArray())
      return arrayToString(value);
    return format("[%s]", summarize(value));
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

  public static String summarize(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection collection = (Collection) value;
      if (collection.size() < 4)
        return collection.toString();
      Iterator<?> i = collection.iterator();
      return format("[%s,%s,%s...;%s]",
          i.next(),
          i.next(),
          i.next(),
          collection.size()
      );
    }
    return value.toString();
  }

  @SuppressWarnings("unchecked")
  public static <R> R invokeMethod(Object target, String methodName, Object[] args) {
    try {
      Method m = findMethod(Objects.requireNonNull(target).getClass(), methodName, args);
      boolean accessible = m.isAccessible();
      try {
        m.setAccessible(true);
        return (R) m.invoke(target, args);
      } finally {
        m.setAccessible(accessible);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T, R> Function<T, R> function(String s, Function<? super T, ? extends R> function) {
    Objects.requireNonNull(s);
    Objects.requireNonNull(function);
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      public <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return new Function<V, R>() {
          @Override
          public R apply(V v) {
            return function.apply(before.apply(v));
          }

          @Override
          public String toString() {
            return format("%s->%s", before, s);
          }
        };
      }

      public <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return new Function<T, V>() {
          @Override
          public V apply(T t) {
            return after.apply(function.apply(t));
          }

          @Override
          public String toString() {
            return format("%s->%s", s, after);
          }
        };
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T> Predicate<T> predicate(String s, Predicate<? super T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return predicate.test(t) && other.test(t);
          }

          @Override
          public String toString() {
            return format("(%s&&%s)", s, other);
          }
        };
      }

      @Override
      public Predicate<T> negate() {
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return !predicate.test(t);
          }

          @Override
          public String toString() {
            return format("!%s", s);
          }
        };
      }

      @Override
      public Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
          @Override
          public boolean test(T t) {
            return predicate.test(t) || other.test(t);
          }

          @Override
          public String toString() {
            return format("(%s||%s)", s, other);
          }
        };
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }
}
