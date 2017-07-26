package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.functions.CrestFunctions;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.ComparisonFailure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum MatcherBuilders {
  ;

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("Convert2Diamond")
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return new AllOf<T>(true, asList(matchers));
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SuppressWarnings("Convert2Diamond")
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return new AnyOf<T>(true, asList(matchers));
  }

  @SuppressWarnings("unchecked")
  public static <I, O, C extends AsObject<? super I, C>> C create(Function<? super I, ? extends O> function) {
    return (C) new AsObject<>(function);
  }

  @SuppressWarnings("unchecked")
  public static <I, C extends AsObject<? super I, C>> C asObject() {
    return (C) create(CrestFunctions.identity());
  }

  public static <I, S extends AsObject<I, S>> AsObject<? super I, S> asObject(String methodName, Object... args) {
    return new AsObject<>(CrestFunctions.invoke(methodName, args));
  }

  public static <I, T extends Comparable<T>> AsComparable<I, T> asComparable(Function<? super I, ? extends T> function) {
    return new AsComparable<>(function);
  }

  public static <T extends Comparable<T>> AsComparable<T, T> asComparable() {
    return asComparable(Function.identity());
  }

  @SuppressWarnings("unchecked")
  public static <I, T extends Comparable<T>> AsComparable<I, T> asComparable(String methodName, Object... args) {
    return (AsComparable<I, T>) new AsComparable<>((Function<? super I, ? extends String>) CrestFunctions.invoke(methodName, args));
  }

  public static <I> AsString<I> asString() {
    return asString(CrestFunctions.stringify());
  }

  public static <I> AsString<I> asString(Function<? super I, ? extends String> function) {
    return new AsString<>(Objects.requireNonNull(function));
  }

  @SuppressWarnings({ "RedundantCast", "unchecked" })
  public static <I> AsString<I> asString(String methodName, Object... args) {
    return asString((Function<? super I, ? extends String>) CrestFunctions.invoke(methodName, args));
  }

  public static <I extends Collection<? extends E>, E> AsStream<I, E> asStream() {
    return new AsStream<>(CrestFunctions.stream());
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
      this.matchers = Objects.requireNonNull(matchers);
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
    public AllOf(boolean showTarget, List<? extends Matcher<? super T>> matchers) {
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
    public AnyOf(boolean showTarget, List<? extends Matcher<? super T>> matchers) {
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

