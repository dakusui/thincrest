package com.github.dakusui.crest;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public enum CrestUtils {
  ;

  static final PrintStream STDOUT = System.out;
  static final PrintStream STDERR = System.err;

  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  public static void suppressStdOutErrIfRunUnderSurefire() {
    if (CrestUtils.isRunUnderSurefire()) {
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
      }));
      System.setErr(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
      }));
    }
  }

  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  public static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setOut(STDERR);
  }

  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }

  /**
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return new DiagnosingMatcher<T>() {
      @Override
      protected boolean matches(Object o, Description mismatch) {
        boolean ret = true;
        for (Matcher<? super T> matcher : matchers) {
          try {
            if (!matcher.matches(o)) {
              if (ret)
                mismatch.appendText("(");
              mismatch.appendText("\n  ");
              mismatch.appendDescriptionOf(matcher).appendText(" ");
              matcher.describeMismatch(o, mismatch);
              ret = false;
            }
          } catch (Exception e) {
            if (ret)
              mismatch.appendText("(");
            mismatch.appendText("\n  ");
            mismatch
                .appendDescriptionOf(matcher)
                .appendText(" on ")
                .appendValue(o)
                .appendText(" ")
                .appendText(String.format("failed with %s(%s)", e.getClass().getCanonicalName(), e.getMessage()));
            ret = false;
          }
        }
        if (!ret)
          mismatch.appendText("\n)");
        return ret;
      }

      @Override
      public void describeTo(Description description) {
        description.appendList("(\n  ", " " + "and" + "\n  ", "\n)", Arrays.stream(matchers).collect(toList()));
      }
    };
  }

  /**
   * TODO:Not yet implemented
   * A bit better version of CoreMatchers.allOf.
   * For example:
   * <pre>assertThat("myValue", allOf(startsWith("my"), containsString("Val")))</pre>
   */
  @SafeVarargs
  public static <T> Matcher<T> anyOf(Matcher<? super T>... matchers) {
    return new DiagnosingMatcher<T>() {
      @Override
      protected boolean matches(Object o, Description mismatch) {
        boolean ret = true;
        for (Matcher<? super T> matcher : matchers) {
          try {
            if (!matcher.matches(o)) {
              if (ret)
                mismatch.appendText("(");
              mismatch.appendText("\n  ");
              mismatch.appendDescriptionOf(matcher).appendText(" ");
              matcher.describeMismatch(o, mismatch);
              ret = false;
            }
          } catch (Exception e) {
            if (ret)
              mismatch.appendText("(");
            mismatch.appendText("\n  ");
            mismatch
                .appendDescriptionOf(matcher)
                .appendText(" on ")
                .appendValue(o)
                .appendText(" ")
                .appendText(String.format("failed with %s(%s)", e.getClass().getCanonicalName(), e.getMessage()));
            ret = false;
          }
        }
        if (!ret)
          mismatch.appendText("\n)");
        return ret;
      }

      @Override
      public void describeTo(Description description) {
        description.appendList("(\n  ", " " + "and" + "\n  ", "\n)", Arrays.stream(matchers).collect(toList()));
      }
    };
  }
}
