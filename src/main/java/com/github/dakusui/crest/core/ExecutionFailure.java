package com.github.dakusui.crest.core;

import org.junit.ComparisonFailure;

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
 *
 * For instance, suppose that an integration test code tries to build a test fixture
 * where a database table is created and data set is loaded into it and then exercises
 * a test case for business logic. And before the exercise, it also verifies such
 * a fixture is sound and worth starting the actual test. In this situation, throwing
 * {@code AssumptionViolatedException} is not a good idea because it will be
 * silently ignored by testing framework (such as JUnit) but it still may suggest
 * some bug in SUT since such preparation is often implemented using functionalities
 * of the SUT.
 */
public class ExecutionFailure extends Error {
  @SuppressWarnings("WeakerAccess")
  public ExecutionFailure(String message, String expected, String actual) {
    super(new ComparisonFailure(message, expected, actual).getMessage());
  }
}
