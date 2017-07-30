package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.InternalUtils;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class InternalUtilsTest {
  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFound$thenExceptionThrown() {
    InternalUtils.findMethod(Object.class, "undefined", new Object[] {});
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFoundBecauseNullNotMatched$thenExceptionThrown() {
    System.out.println(InternalUtils.findMethod(Object.class, "wait", new Object[] { null }));
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFoundBecauseArgumentNotMatched$thenExceptionThrown() {
    System.out.println(InternalUtils.findMethod(Object.class, "wait", new Object[] { "hello" }));
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenMultipleMethodsFound$thenExceptionThrown() {
    System.out.println(InternalUtils.findMethod(InternalUtilsTest.class, "dummy", new Object[] { "hello" }));
  }

  @Test
  public void tryToFindMethod$whenOverloadedMethod$thenLooksGood() {
    Method m = InternalUtils.findMethod(Object.class, "wait", new Object[] { 0L });
    String methodInfo = String.format("%s/%s", m.getName(), m.getParameterTypes().length);

    assertEquals(
        "wait/1",
        methodInfo
    );
  }

  @Test(expected = IllegalStateException.class)
  public void givenUnexpectedState$whenRequireState$thenIllegalStateExceptionThrown() {
    InternalUtils.requireState(false);
  }

  @Test
  public void givenExpectedState$whenRequireState$thenIllegalStateExceptionThrown() {
    InternalUtils.requireState(true);
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFound$thenExceptionThrown'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public void dummy(Object arg) {
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFound$thenExceptionThrown'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public void dummy(String arg) {
  }
}
