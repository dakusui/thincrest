package com.github.dakusui.crest.utils.ut;

import com.github.dakusui.crest.utils.printable.Functions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collections;

import static com.github.dakusui.crest.utils.printable.Functions.*;
import static com.github.dakusui.crest.utils.ut.FaultSourceTest.assertEquals;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RunWith(Enclosed.class)
public class FunctionsTest {
  public static class ElementAtTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      Assert.assertEquals(
          200,
          Functions.elementAt(1).apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "elementAt[123]",
          Functions.elementAt(123).toString()
      );
    }
  }

  public static class SizeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          (Integer) 3,
          Functions.size().apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "size",
          Functions.size().toString()
      );
    }
  }

  public static class StreamTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          asList(100, 200, 300),
          stream().apply(asList(100, 200, 300)).collect(toList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "stream",
          stream().toString()
      );
    }
  }

  public static class InvokeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          12,
          invoke("length").apply("Hello, world")
      );

      assertEquals(
          true,
          invoke("equals", "Hello, world").apply("Hello, world")
      );

      assertEquals(
          false,
          invoke("equals", "Hello, world").apply("Hello, world!")
      );
    }

    @Test
    public void whenToString$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "@equals[Hello, world]",
          invoke("equals", "Hello, world").toString()
      );

    }
  }

  public static class StringifyTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "[]",
          stringify().apply(Collections.emptyList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() throws FaultSourceTest.TestFailure {
      assertEquals(
          "toString",
          stringify().toString()
      );

    }
  }

}
