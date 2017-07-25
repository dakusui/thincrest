package com.github.dakusui.crest;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Function;
import java.util.function.Predicate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Enclosed.class)
public class FormattableTest {
  public static class FunctionTest extends CrestUnit {
    Function<String, String> appendA = Formattable.function("append[A]", s -> s + "A");
    Function<String, String> appendB = Formattable.function("append[B]", s -> s + "B");

    @Test
    public void givenFunctionReturnedByCompose$whenApply$thenWorksRight() {
      Function<String, String> composed = appendA.compose(appendB);

      assertEquals("HELLO:BA", composed.apply("HELLO:"));
    }

    @Test
    public void givenFunctionReturnedByCompose$whenToString$thenLooksGood() {
      Function<String, String> composed = appendA.compose(appendB);

      assertEquals("append[B]->append[A]", composed.toString());
    }

    @Test
    public void givenFunctionReturnedByAndThen$whenApply$thenWorksRight() {
      Function<String, String> andThen = appendA.andThen(appendB);

      assertEquals("HELLO:AB", andThen.apply("HELLO:"));
    }

    @Test
    public void givenFunctionReturnedByAndThen$whenToString$thenLooksGood() {
      Function<String, String> andThen = appendA.andThen(appendB);

      assertEquals("append[A]->append[B]", andThen.toString());
    }
  }

  public static class PredicateTest extends CrestUnit {
    Predicate<String> isA = Formattable.predicate("is[A]", "A"::equals);
    Predicate<String> isB = Formattable.predicate("is[B]", "B"::equals);

    @Test
    public void givenPredicateReturnedByAnd$whenTest$thenWorksRight() {
      assertFalse(isA.and(isB).test("A"));
      assertFalse(isA.and(isB).test("B"));
      assertTrue(isA.and(isB.negate()).test("A"));
    }

    @Test
    public void givenPredicateReturnedByAnd$whenToString$thenLooksGood() {
      assertEquals("is[A]&&is[B]", isA.and(isB).toString());
    }

    @Test
    public void givenPredicateReturnedByOr$whenTest$thenWorksRight() {
      assertTrue(isA.or(isB).test("B"));
      assertTrue(isA.or(isB).test("A"));
      assertFalse(isA.or(isB).test("C"));
    }

    @Test
    public void givenPredicateReturnedByOr$whenToString$thenWorksRight() {
      assertEquals("(is[A]||is[B])", isA.or(isB).toString());
    }

    @Test
    public void givenPredicateReturnedByNegate$whenTest$thenWorksRight() {
      assertFalse(isA.negate().test("A"));
      assertTrue(isA.negate().test("B"));
    }

    @Test
    public void givenPredicateReturnedByNegate$whenToString$thenWorksRight() {
      assertEquals("!is[A]", isA.negate().toString());
    }
  }
}
