package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.CrestFunctions;
import com.github.dakusui.crest.CrestUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class CrestFunctionsTest {
  public static class ElementAtTest extends CrestUnit {
    @Test
    public void whenApplied$thenLooksGood() {
      Assert.assertEquals(
          200,
          CrestFunctions.elementAt(1).apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "elementAt[123]",
          CrestFunctions.elementAt(123).toString()
      );
    }
  }

  public static class SizeTest extends CrestUnit {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          (Integer) 3,
          CrestFunctions.size().apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "size",
          CrestFunctions.size().toString()
      );
    }
  }

  public static class StreamTest extends CrestUnit {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          asList(100, 200, 300),
          CrestFunctions.stream().apply(asList(100, 200, 300)).collect(toList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "stream",
          CrestFunctions.stream().toString()
      );
    }
  }

  public static class InvokeTest extends CrestUnit {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          12,
          CrestFunctions.invoke("length").apply("Hello, world")
      );

      assertEquals(
          true,
          CrestFunctions.invoke("equals", "Hello, world").apply("Hello, world")
      );

      assertEquals(
          false,
          CrestFunctions.invoke("equals", "Hello, world").apply("Hello, world!")
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "invoke[equals,[Hello, world]]",
          CrestFunctions.invoke("equals", "Hello, world").toString()
      );

    }
  }

  public static class StringifyTest extends CrestUnit {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          "[]",
          CrestFunctions.stringify().apply(Collections.emptyList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "toString",
          CrestFunctions.stringify().toString()
      );

    }
  }

}
