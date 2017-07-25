package com.github.dakusui.crest;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum CrestMatchers {
  ;

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return new AllOf<>(true, asList(matchers));
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return new AnyOf<>(true, asList(matchers));
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
      boolean ret = !until();
      List<Description> mismatches = new LinkedList<>();
      for (Matcher<? super T> each : this.matchers) {
        Description mismatchForEach = new StringDescription();
        boolean current = tryToMatch(each, o, mismatchForEach, exceptions);
        if (!current)
          mismatches.add(mismatchForEach);
        ret = next(ret, current);
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
      for (Exception e : exceptions) {
        mismatch.appendText("\n" + indent + e.getMessage());
        for (StackTraceElement s : e.getStackTrace()) {
          mismatch.appendText("\n" + indent + "  " + s.toString());
        }
      }
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

  static class AllOf<T> extends IndentManagedDiagnosingMatcher<T> {
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

  static class AnyOf<T> extends IndentManagedDiagnosingMatcher<T> {
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

