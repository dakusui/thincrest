package com.github.dakusui.crest.core;

import com.github.dakusui.crest.Crest;
import org.junit.ComparisonFailure;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Thrown when a test programs detects that a certain runtime requirement to exercise
 * a test is not satisfied.
 * <p>
 * For completely predictable tests or tests expected to be so, such as unit tests,
 * this shouldn't be thrown.
 * <p>
 * However in non-unit testing phases, where tests depend on external conditions
 * outside JVM, even if inputs are all valid, still preconditions for them can be
 * unsatisfied. This is an exception to be thrown in such cases.
 * <p>
 * For instance, suppose that an integration test code tries to build a test fixture
 * where a database table is created and data set is loaded into it and then exercises
 * a test case for business logic. And before the exercise, it also verifies such
 * a fixture is sound and worth starting the actual test. In this situation, throwing
 * {@code AssumptionViolatedException} is not a good idea because it will be
 * silently ignored by testing framework (such as JUnit) but it still may suggest
 * some bug in SUT since such preparation is often implemented using functionalities
 * of the SUT.
 *
 * @see Crest#requireThat
 */
public class ExecutionFailure extends Error {
  private final List<Throwable> childExceptions;

  @SuppressWarnings("WeakerAccess")
  public ExecutionFailure(String message, String expected, String actual, List<Throwable> childExceptions) {
    super(new ComparisonFailure(message, expected, actual).getMessage());
    this.childExceptions = requireNonNull(childExceptions);
  }

  public List<Throwable> getChildExceptions() {
    return unmodifiableList(this.childExceptions);
  }

  /*
  @Override
  public String getMessage() {
    return super.getMessage() + childExceptions.stream().flatMap(throwable -> Arrays.stream(throwable.getStackTrace())).map(new Function<StackTraceElement, String>() {
      @Override
      public String apply(StackTraceElement stackTraceElement) {
        return stackTraceElement.toString();
      }
    }).collect(Collectors.joining(String.format("%n")));
  }
  */

  public static class Builder {
    private String          message;
    private String          expected;
    private String          actual;
    private List<Throwable> childExceptions = new LinkedList<>();

    public Builder() {
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder expected(String expected) {
      this.expected = expected;
      return this;
    }

    public Builder actual(String actual) {
      this.actual = actual;
      return this;
    }

    public Builder add(Throwable child) {
      this.childExceptions.add(child);
      return this;
    }

    public ExecutionFailure build() {
      return new ExecutionFailure(this.message, this.actual, this.expected, childExceptions);
    }
  }
}
