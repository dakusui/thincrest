package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.utils.TestBase;
import com.github.dakusui.crest.utils.printable.Functions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Functions.*;
import static com.github.dakusui.crest.utils.printable.Predicates.alwaysTrue;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class CrestFunctionsTest {
  public static class Compilability extends TestBase {
    @Test
    public void whenAsObject$thenCompilableWithPredefinedFunctions() {
      List<String> aList = asList("A", "B", "C");

      assertThat(
          aList,
          allOf(
              asObject(stringify()).check(alwaysTrue()).matcher(),
              asObject(invoke("toString")).check(alwaysTrue()).matcher(),
              asObject(elementAt(0)).check(alwaysTrue()).matcher(),
              asObject(size()).check(alwaysTrue()).matcher(),
              asObject(stream()).check(alwaysTrue()).matcher(),
              asObject("toString").check(alwaysTrue()).matcher()
          )
      );
    }
  }

  public static class ElementAtTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      Assert.assertEquals(
          200,
          Functions.elementAt(1).apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "->at[123]",
          Functions.elementAt(123).toString()
      );
    }
  }

  public static class SizeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          (Integer) 3,
          Functions.size().apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "->size",
          Functions.size().toString()
      );
    }
  }

  public static class StreamTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          asList(100, 200, 300),
          stream().apply(asList(100, 200, 300)).collect(toList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "->stream",
          stream().toString()
      );
    }
  }

  public static class InvokeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
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
    public void whenToString$thenLooksGood() {
      assertEquals(
          ".equals(\"Hello, world\")",
          invoke("equals", "Hello, world").toString()
      );

    }
  }

  public static class StringifyTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          "[]",
          stringify().apply(Collections.emptyList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "->stringify",
          stringify().toString()
      );

    }
  }
}
