package com.github.dakusui.crest.utils.ut;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.github.dakusui.crest.utils.ut.FaultSourceFaultSource.PRODUCTION;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

@RunWith(Enclosed.class)
public class FaultSourceTest {
  public static class Require {
    @Test
    public void given_HELLO_$whenRequireValue_HELLO_$thenValueReturned() throws TestFailure {
      FaultSourceTest.assertEquals("HELLO!", PRODUCTION.requireValue("HELLO!"::equals, "HELLO!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_HELLO_$whenRequireValue_WORLD_$thenIllegalArgumentExceptionThrown() {
      PRODUCTION.requireValue("WORLD"::equals, "HELLO");
    }

    @Test
    public void given_HELLO_$whenRequireNext_HELLO_$thenDone() throws TestFailure {
      assertEquals("HELLO", PRODUCTION.requireNext("HELLO"::equals, "HELLO"));
    }

    @Test(expected = NoSuchElementException.class)
    public void given_HELLO_$whenRequireNext_WORLD_$thenIllegalArgumentExceptionThrown() {
      PRODUCTION.requireNext("WORLD"::equals, "HELLO");
    }

    @Test
    public void given_HELLO_$whenRequireState_HELLO_$thenDone() throws TestFailure {
      assertEquals("HELLO", PRODUCTION.requireState("HELLO"::equals, "HELLO"));
    }

    @Test(expected = IllegalStateException.class)
    public void given_HELLO_$whenRequireState_WORLD_$thenIllegalArgumentExceptionThrown() {
      PRODUCTION.requireState("WORLD"::equals, "HELLO");
    }

    @Test
    public void givenNotInterrupted$whenRequireNotInterrupted$thenDone() throws InterruptedException {
      PRODUCTION.requireNotInterrupted();
    }

    @Test(expected = InterruptedException.class)
    public void givenInterrupted$whenRequireNotInterrupted$thenInterruptedExceptionThrown() throws InterruptedException {
      Thread.currentThread().interrupt();
      PRODUCTION.requireNotInterrupted();
    }

    @SuppressWarnings("RedundantTypeArguments")
    @Test(expected = IllegalArgumentException.class)
    public void given_HELLO_$whenRequire_WORLD_withMessage$thenIllegalArgumentExceptionThrown() throws TestFailure {
      try {
        PRODUCTION.<String>require(
            "WORLD"::equals,
            "HELLO",
            PRODUCTION::exceptionForIllegalValue,
            c -> v -> String.format("condition=%s;value=%s", c, v));
      } catch (IllegalArgumentException e) {
        FaultSourceTest.assertThat(
            e.getMessage(),
            CoreMatchers.allOf(
                CoreMatchers.containsString("condition="),
                CoreMatchers.containsString("value=HELLO")
            )
        );
        throw e;
      }
    }
  }

  public static class RequireNonNull {
    @Test
    public void givenNonNull$whenRequireNonNull$thenValueReturned() throws TestFailure {
      assertEquals("Hello", PRODUCTION.requireNonNull("Hello"));
    }

    @Test
    public void givenNonNull$whenRequireNonNullWithMessage$thenValueReturned() throws TestFailure {
      assertEquals("Hello!", PRODUCTION.requireNonNull("Hello!", "MESSAGE"));
    }

    @Test(expected = NullPointerException.class)
    public void givenNull$whenRequireNonNull$thenNullPointerExceptionThrown() {
      PRODUCTION.requireNonNull(null);
    }

    @Test(expected = NullPointerException.class)
    public void givenNull$whenRequireNonNullWithMessage$thenNullPointerExceptionThrown() throws TestFailure {
      try {
        PRODUCTION.requireNonNull(null, "value was null");
      } catch (NullPointerException e) {
        assertEquals("value was null", e.getMessage());
        throw e;
      }
    }
  }

  public static class ImpossibleLineReached {
    @Test(expected = AssertionError.class)
    public void whenImpossibleLineReached$thenAssertionErrorThrown() {
      throw PRODUCTION.impossibleLineReached();
    }

    @Test(expected = AssertionError.class)
    public void whenImpossibleLineReached$thenAssertionErrorThrownWithMessage() throws TestFailure {
      String message = "impossibleLineReached";
      try {
        throw PRODUCTION.impossibleLineReached(message);
      } catch (AssertionError e) {
        assertEquals(message, e.getMessage());
        throw e;
      }
    }
  }

  public static class FailureCaught {
    @Test(expected = RuntimeException.class)
    public void givenIOException$whenFailureCaught$thenIOExceptionIsWrapped() throws TestFailure {
      try {
        throw PRODUCTION.failureCaught(new IOException("Hello, world"));
      } catch (RuntimeException e) {
        assertThat(
            e.getClass(),
            equalTo(RuntimeException.class)
        );
        assertThat(
            e.getCause(),
            instanceOf(IOException.class)
        );
        assertThat(
            e.getMessage(),
            equalTo("Hello, world")
        );
        throw e;
      }
    }

    @Test(expected = TestError.class)
    public void givenError$whenFailureCaught$thenIOExceptionIsWrapped() throws TestFailure {
      throw PRODUCTION.failureCaught(new TestError());
    }
  }

  static void assertEquals(Object expected, Object actual) throws TestFailure {
    if (!Objects.equals(expected, actual))
      throw new TestFailure(String.format("expected=%s%nactual=%s", expected, actual));
  }

  static void assertThat(Object value, Matcher matcher) throws TestFailure {
    if (!matcher.matches(value))
      throw new TestFailure(String.format("'%s' was not satisfied by '%s'", matcher, value));
  }


  static class TestFailure extends Throwable {
    TestFailure(String message) {
      super(message);
    }
  }

  static class TestError extends Error {
  }
}
