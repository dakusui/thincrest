package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.matcherbuilders.primitives.*;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.ComparisonFailure;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * A facade class of 'thincrest'.
 */
public enum Crest {
  ;

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("Convert2Diamond")
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return new AllOf<T>(true, Arrays.asList(matchers));
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("Convert2Diamond")
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return new AnyOf<T>(true, Arrays.asList(matchers));
  }

  public static <I, S extends AsObject<I, I, S>> AsObject<I, I, S> asObject() {
    return new AsObject<>(CrestFunctions.identity());
  }

  public static <I, O, S extends AsObject<I, O, S>> AsObject<I, O, S> asObject(String methodName, Object... args) {
    return new AsObject<>(CrestFunctions.<I, O>invoke(methodName, args));
  }

  public static <I, O, S extends AsObject<I, O, S>> AsObject<I, O, S> asObject(Function<? super I, ? extends O> function) {
    return new AsObject<>(function);
  }

  public static AsBoolean<Boolean> asBoolean() {
    return asBoolean(CrestFunctions.identity());
  }

  public static <I> AsBoolean<I> asBoolean(String methodName, Object... args) {
    return asBoolean(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Boolean.class)));
  }

  public static <I> AsBoolean<I> asBoolean(Predicate<? super I> predicate) {
    requireNonNull(predicate);
    return asBoolean(Formattable.function(predicate.toString(), predicate::test));
  }

  public static <I> AsBoolean<I> asBoolean(Function<? super I, Boolean> function) {
    return new AsBoolean<>(function);
  }

  public static <I> AsByte<I> asByte(Function<? super I, Byte> function) {
    return new AsByte<>(function);
  }

  public static <I> AsByte<I> asByte(String methodName, Object... args) {
    return asByte(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Byte.class)));
  }

  public static AsByte<Byte> asByte() {
    return asByte(CrestFunctions.identity());
  }

  public static <I> AsChar<I> asChar(Function<? super I, Character> function) {
    return new AsChar<>(function);
  }

  public static <I> AsChar<I> asChar(String methodName, Object... args) {
    return asChar(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Character.class)));
  }

  public static AsChar<Character> asChar() {
    return asChar(CrestFunctions.identity());
  }

  public static <I> AsShort<I> asShort(Function<? super I, Short> function) {
    return new AsShort<>(function);
  }

  public static <I> AsShort<I> asShort(String methodName, Object... args) {
    return asShort(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Short.class)));
  }

  public static AsShort<Short> asShort() {
    return asShort(CrestFunctions.identity());
  }

  public static <I> AsInteger<I> asInteger(Function<? super I, Integer> function) {
    return new AsInteger<>(function);
  }

  public static <I> AsInteger<I> asInteger(String methodName, Object... args) {
    return asInteger(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Integer.class)));
  }

  public static AsInteger<Integer> asInteger() {
    return asInteger(CrestFunctions.identity());
  }

  public static <I> AsLong<I> asLong(Function<? super I, Long> function) {
    return new AsLong<>(function);
  }

  public static <I> AsLong<I> asLong(String methodName, Object... args) {
    return asLong(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Long.class)));
  }

  public static AsLong<Long> asLong() {
    return asLong(CrestFunctions.identity());
  }

  public static <I> AsFloat<I> asFloat(Function<? super I, Float> function) {
    return new AsFloat<>(function);
  }

  public static <I> AsFloat<I> asFloat(String methodName, Object... args) {
    return asFloat(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Float.class)));
  }

  public static AsFloat<Float> asFloat() {
    return asFloat(CrestFunctions.identity());
  }

  public static <I> AsDouble<I> asDouble(Function<? super I, Double> function) {
    return new AsDouble<>(function);
  }

  public static <I> AsDouble<I> asDouble(String methodName, Object... args) {
    return asDouble(CrestFunctions.invoke(methodName, (Object[]) args).andThen(CrestFunctions.cast(Double.class)));
  }

  public static AsDouble<Double> asDouble() {
    return asDouble(CrestFunctions.identity());
  }

  /*
   * Casts a given object into the given comparable type
   */
  public static <I extends Comparable<? super I>, S extends AsComparable<I, I, S>>
  S asComparableOf(Class<I> type) {
    return asComparable(CrestFunctions.cast(type));
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparable(Function<? super I, ? extends T> function) {
    return (S) new AsComparable<>(function);
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<? super T>, S extends AsComparable<I, T, S>>
  S asComparableOf(Class<T> type, String methodName, Object... args) {
    return (S) asComparable(CrestFunctions.invoke(methodName, (Object[]) args).<T>andThen(CrestFunctions.cast(type)));
  }

  public static <I> AsString<I> asString() {
    return asString(CrestFunctions.stringify());
  }

  public static <I> AsString<I> asString(Function<? super I, ? extends String> function) {
    return new AsString<>(requireNonNull(function));
  }

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <I> AsString<I> asString(String methodName, Object... args) {
    return asString((Function<? super I, ? extends String>) CrestFunctions.invoke(methodName, args));
  }

  public static <I extends Collection<? extends E>, E> AsStream<I, E> asStream() {
    return new AsStream<>(CrestFunctions.stream());
  }

  public static <I extends Collection<?>> AsList<? super I, ?> asList() {
    return asListOf(Object.class, CrestFunctions.collectionToList());
  }

  public static <I> AsList<? super I, ?> asList(Function<? super I, ? extends List<Object>> function) {
    return asListOf(Object.class, function);
  }

  public static <I extends Collection<E>, E> AsList<I, E> asListOf(Class<E> type) {
    return asListOf(type, CrestFunctions.collectionToList());
  }

  public static <I, E> AsList<I, E> asListOf(@SuppressWarnings("unused") Class<E> type, Function<? super I, ? extends List<E>> function) {
    return new AsList<>(function);
  }

  public static <I extends Map, SELF extends AsMap<I, Object, Object, SELF>> SELF asMap() {
    return asMapOf(Object.class, Object.class, Formattable.function("mapToMap", o -> new HashMap<>()));
  }

  public static <I, SELF extends AsMap<I, Object, Object, SELF>> SELF asMap(Function<? super I, ? extends Map<Object, Object>> function) {
    return asMapOf(Object.class, Object.class, function);
  }

  public static <I extends Map<K, V>, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType) {
    return asMapOf(keyType, valueType, CrestFunctions.identity());
  }

  @SuppressWarnings("unchecked")
  public static <I, K, V, SELF extends AsMap<I, K, V, SELF>> SELF asMapOf(Class<K> keyType, Class<V> valueType, Function<? super I, ? extends Map<K, V>> function) {
    requireNonNull(keyType);
    requireNonNull(valueType);
    return (SELF) new AsMap<I, K, V, SELF>(function);
  }

  public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
    assertThat("", actual, matcher);
  }

  public static <T> void assertThat(String message, T actual, Matcher<? super T> matcher) {
    if (!matcher.matches(actual)) {
      Description description = new StringDescription();
      description.appendText(message).appendText("\nExpected: ").appendDescriptionOf(matcher).appendText("\n     but: ");
      matcher.describeMismatch(actual, description);
      Description actualDescription = new StringDescription();
      matcher.describeMismatch(actual, actualDescription);
      throw new ComparisonFailure(
          description.toString(),
          new StringDescription().appendDescriptionOf(matcher).toString(),
          actualDescription.toString());
    }
  }

  static abstract class IndentManagedDiagnosingMatcher<T> extends DiagnosingMatcher<T> {
    private static ThreadLocal<Integer> indent = new ThreadLocal<>();
    final boolean                                  topLevel;
    final Collection<? extends Matcher<? super T>> matchers;

    IndentManagedDiagnosingMatcher(boolean topLevel, Collection<? extends Matcher<? super T>> matchers) {
      this.topLevel = topLevel;
      this.matchers = requireNonNull(matchers);
    }

    @Override
    protected final boolean matches(Object o, Description mismatch) {
      enter();
      try {
        if (topLevel) {
          mismatch.appendText("when x=");
          mismatch.appendValue(o);
          mismatch.appendText("; then ");
        }

        List<Exception> exceptions = new LinkedList<>();
        boolean ret = matches(o, mismatch, exceptions);
        for (Exception e : exceptions) {
          mismatch.appendText("\n" + indent() + e.getMessage());
          for (StackTraceElement s : e.getStackTrace()) {
            mismatch.appendText("\n" + indent() + "  " + s.toString());
          }
        }
        return ret;
      } finally {
        leave();
      }
    }

    @Override
    final public void describeTo(Description description) {
      enter();
      try {
        description.appendList(
            String.format("%s:[%n  ", name()) + indent(),
            String.format("%n%s  ", indent()),
            String.format("%n%s]%s", indent(), this.topLevel ? "->true" : ""),
            matchers
        );
      } finally {
        leave();
      }
    }

    boolean matches(Object o, Description mismatch, List<Exception> exceptions) {
      List<Exception> exceptions_ = new LinkedList<>();
      boolean ret = !until();
      List<Description> mismatches = new LinkedList<>();
      for (Matcher<? super T> each : this.matchers) {
        Description mismatchForEach = new StringDescription();
        boolean current = tryToMatch(each, o, mismatchForEach, exceptions_);
        if (!current)
          mismatches.add(mismatchForEach);
        ret = next(ret, current) && exceptions_.isEmpty();
      }
      String indent = indent();
      mismatch.appendText(mismatches.stream(
          ).map(Object::toString
          ).collect(
          toList()
          ).stream(
          ).collect(Collectors.joining(
          String.format("%n"),
          String.format("%s%s:[%n", indent.length() >= 2 ? indent.substring(2) : "", name()),
          String.format("%n%s]->%s", indent, ret)
          ))
      );
      exceptions.addAll(exceptions_);
      return ret;
    }

    protected abstract boolean until();

    boolean tryToMatch(Matcher<? super T> matcher, Object o, Description mismatch, List<Exception> exceptions) {
      Exception exception = null;
      try {
        return matcher.matches(o);
      } catch (Exception e) {
        exception = e;
        return false;
      } finally {
        mismatch.appendText("  " + indent());
        if (exception == null) {
          matcher.describeMismatch(o, mismatch);
        } else {
          exceptions.add(exception);
          mismatch.appendDescriptionOf(matcher)
              .appendText(" ")
              .appendText(String.format("failed with %s(%s)", exception.getClass().getCanonicalName(), exception.getMessage()));
        }
      }
    }

    abstract boolean next(boolean previous, boolean current);

    abstract String name();

    static void enter() {
      indent.set(
          indent.get() == null ?
              0 :
              indent.get() + 1
      );
    }

    static void leave() {
      indent.set(
          indent.get() <= 0 ?
              null :
              indent.get() - 1
      );
    }

    static int indentLevel() {
      return indent.get();
    }

    static String indent() {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < indentLevel(); i++) {
        b.append("  ");
      }
      return b.toString();
    }
  }

  public static class AllOf<T> extends IndentManagedDiagnosingMatcher<T> {
    AllOf(boolean showTarget, List<? extends Matcher<? super T>> matchers) {
      super(showTarget, matchers);
    }

    @Override
    boolean next(boolean previous, boolean current) {
      return previous && current;
    }

    @Override
    String name() {
      return "and";
    }

    @Override
    protected boolean until() {
      return false;
    }
  }

  public static class AnyOf<T> extends IndentManagedDiagnosingMatcher<T> {
    AnyOf(boolean showTarget, List<? extends Matcher<? super T>> matchers) {
      super(showTarget, matchers);
    }

    @Override
    boolean next(boolean previous, boolean current) {
      return previous || current;
    }

    @Override
    String name() {
      return "or";
    }

    @Override
    protected boolean until() {
      return true;
    }
  }
}

