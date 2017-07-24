package com.github.dakusui.crest;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
    return new AllOf<>(true, matchers);
  }

  /**
   * A bit better version of CoreMatchers.anyOf.
   * For example:
   * <pre>assertThat("myValue", anyOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return new AnyOf<>(true, matchers);
  }

  static abstract class IndentManagedDiagnosingMatcher<T> extends DiagnosingMatcher<T> {
    private static ThreadLocal<Integer> indent = new ThreadLocal<>();
    final boolean              showTarget;
    final Matcher<? super T>[] matchers;

    IndentManagedDiagnosingMatcher(boolean showTarget, Matcher<? super T>[] matchers) {
      this.showTarget = showTarget;
      this.matchers = matchers;
    }

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
    AllOf(boolean showTarget, Matcher<? super T>[] matchers) {
      super(showTarget, matchers);
    }

    @Override
    protected boolean matches(Object o, Description mismatch) {
      enter();
      try {
        List<Exception> exceptions = new LinkedList<>();
        boolean ret = true;
        if (showTarget) {
          mismatch.appendText("when x=");
          mismatch.appendValue(o);
          mismatch.appendText("; then ");
        }
        for (Matcher<? super T> matcher : this.matchers) {
          try {
            if (!matcher.matches(o)) {
              if (ret)
                mismatch.appendText("and(");
              mismatch.appendText("\n  " + indent());
              matcher.describeMismatch(o, mismatch);
              ret = false;
            }
          } catch (Exception e) {
            exceptions.add(e);
            if (ret)
              mismatch.appendText("and(");
            mismatch.appendText("\n  " + indent());
            mismatch
                .appendDescriptionOf(matcher)
                .appendText(" ")
                .appendText(String.format("failed with %s(%s)", e.getClass().getCanonicalName(), e.getMessage()));
            ret = false;
          }
        }
        if (!ret) {
          mismatch.appendText("\n" + indent() + ")");
        }
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
    public void describeTo(Description description) {
      enter();
      try {
        description.appendList(
            "and(\n  " + indent(),
            "\n  " + indent(),
            "\n" + indent() + ")",
            Arrays.stream(matchers).collect(toList()));
      } finally {
        leave();
      }
    }
  }

  static class AnyOf<T> extends IndentManagedDiagnosingMatcher<T> {
    AnyOf(boolean showTarget, Matcher<? super T>[] matchers) {
      super(showTarget, matchers);
    }

    @Override
    protected boolean matches(Object o, Description mismatch) {
      enter();
      try {
        List<Exception> exceptions = new LinkedList<>();
        boolean ret = false;
        if (showTarget) {
          mismatch.appendText("when x=");
          mismatch.appendValue(o);
          mismatch.appendText("; then ");
        }
        boolean firstTime = true;
        for (Matcher<? super T> matcher : this.matchers) {
          if (firstTime)
            mismatch.appendText("or(");
          firstTime = false;
          ret = tryToMatch(matcher, o, mismatch, exceptions);
          if (ret)
            break;
        }
        if (!firstTime) {
          mismatch.appendText("\n" + indent() + ")");
        }
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

    private boolean tryToMatch(Matcher<? super T> matcher, Object o, Description mismatch, List<Exception> exceptions) {
      boolean ret;
      try {
        ret = matcher.matches(o);
        if (!ret) {
          mismatch.appendText("\n  " + indent());
          matcher.describeMismatch(o, mismatch);
        }
      } catch (Exception e) {
        ret = false;
        mismatch.appendText("\n  " + indent());
        mismatch
            .appendDescriptionOf(matcher)
            .appendText(" ")
            .appendText(String.format("failed with %s(%s)", e.getClass().getCanonicalName(), e.getMessage()));
        exceptions.add(e);
      }
      return ret;
    }

    @Override
    public void describeTo(Description description) {
      enter();
      try {
        description.appendList(
            "or(\n  " + indent(),
            "\n  " + indent(),
            "\n" + indent() + ")",
            Arrays.stream(matchers).collect(toList()));
      } finally {
        leave();
      }
    }
  }
}
