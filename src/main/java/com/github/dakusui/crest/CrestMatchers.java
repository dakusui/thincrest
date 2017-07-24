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
    final boolean              topLevel;
    final Matcher<? super T>[] matchers;

    IndentManagedDiagnosingMatcher(boolean topLevel, Matcher<? super T>[] matchers) {
      this.topLevel = topLevel;
      this.matchers = matchers;
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
        if (!ret) {
          mismatch.appendText("\n" + indent());
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
    final public void describeTo(Description description) {
      enter();
      try {
        description.appendList(
            String.format("%s:\n  ", name()) + indent(),
            "\n  " + indent(),
            "\n",
            Arrays.stream(matchers).collect(toList()));
      } finally {
        leave();
      }
    }

    private String name() {
      return this.getClass().getSimpleName().toLowerCase();
    }

    boolean matches(Object o, Description mismatch, List<Exception> exceptions) {
      boolean ret = !until();
      for (Matcher<? super T> matcher : this.matchers) {
        ret = tryToMatch(matcher, o, mismatch, exceptions);
        //        if (ret == until())
        //          break;
      }
      for (Exception e : exceptions) {
        mismatch.appendText("\n" + indent() + e.getMessage());
        for (StackTraceElement s : e.getStackTrace()) {
          mismatch.appendText("\n" + indent() + "  " + s.toString());
        }
      }
      return ret;
    }

    protected abstract boolean until();

    boolean tryToMatch(Matcher<? super T> matcher, Object o, Description mismatch, List<Exception> exceptions) {
      boolean ret;
      try {
        if (!(ret = matcher.matches(o))) {
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
    protected boolean until() {
      return false;
    }
  }

  static class AnyOf<T> extends IndentManagedDiagnosingMatcher<T> {
    AnyOf(boolean showTarget, Matcher<? super T>[] matchers) {
      super(showTarget, matchers);
    }

    @Override
    protected boolean until() {
      return true;
    }
  }
}

