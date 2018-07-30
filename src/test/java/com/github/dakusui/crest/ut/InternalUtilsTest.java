package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.InternalUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

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

  public static class TestList extends LinkedList {
    @Override
    public String get(int i) {
      return "hello";
    }
  }

  @Test
  public void tryToFindMethod$whenOverridden$thenLooksGood() throws InvocationTargetException, IllegalAccessException {
    Method m = InternalUtils.findMethod(TestList.class, "get", new Object[] { 0 });

    assertEquals(
        "hello",
        m.invoke(new TestList(), 100)
    );
  }

  @Test
  public void givenStringContainingControlSequences$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "\" \\n\\t\\\"\"",
        InternalUtils.formatValue(" \n\t\"")
    );
  }

  @Test
  public void givenChar_$r_$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "\"\\r\"",
        InternalUtils.formatValue('\r')
    );
  }

  @Test
  public void givenNull$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "null",
        InternalUtils.formatValue(null)
    );
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
