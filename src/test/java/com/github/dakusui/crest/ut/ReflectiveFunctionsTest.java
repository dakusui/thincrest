package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.utils.ReflectiveFunctions;
import com.github.dakusui.crest.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class ReflectiveFunctionsTest {
  public static class InvokeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          12,
          ReflectiveFunctions.invoke("length").apply("Hello, world")
      );

      assertEquals(
          true,
          ReflectiveFunctions.invoke("equals", "Hello, world").apply("Hello, world")
      );

      assertEquals(
          false,
          ReflectiveFunctions.invoke("equals", "Hello, world").apply("Hello, world!")
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          ".equals(\"Hello, world\")",
          ReflectiveFunctions.invoke("equals", "Hello, world").toString()
      );
    }
  }
}
