package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.functions.CrestPredicates;
import com.github.dakusui.crest.utils.TestBase;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Enclosed.class)
public class CrestPredicatesTest {
  public static class IsNullTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.isNull().test(null));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.isNull().test("Hello"));
    }
  }

  public static class IsNotNullTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.isNotNull().test("HELLO"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.isNotNull().test(null));
    }
  }

  public static class EqTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      TestCase.assertTrue(CrestPredicates.eq(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.eq(100).test(99));
      assertFalse(CrestPredicates.eq(100).test(101));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("=[123]", CrestPredicates.eq(123).toString());
    }
  }

  public static class GtTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.gt(100).test(101));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.gt(100).test(100));
      assertFalse(CrestPredicates.gt(100).test(99));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(">[123]", CrestPredicates.gt(123).toString());
    }
  }

  public static class GeTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.ge(100).test(101));
      assertTrue(CrestPredicates.ge(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.ge(100).test(99));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(">=[123]", CrestPredicates.ge(123).toString());
    }
  }

  public static class LtTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.lt(100).test(99));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.lt(100).test(100));
      assertFalse(CrestPredicates.lt(100).test(101));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("<[123]", CrestPredicates.lt(123).toString());
    }
  }

  public static class LeTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.le(100).test(99));
      assertTrue(CrestPredicates.le(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.le(100).test(101));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("<=[123]", CrestPredicates.le(123).toString());
    }
  }

  public static class ContainsOnlyTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(
          CrestPredicates.containsOnly(Arrays.asList("a", "b")).test(Collections.singletonList("a"))
      );
      assertTrue(
          CrestPredicates.containsOnly(Arrays.asList("a", "b")).test(Arrays.asList("a", "b"))
      );
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(
          CrestPredicates.containsOnly(Arrays.asList("a", "b")).test(Arrays.asList("a", "b", "c"))
      );
    }
  }

  public static class AllMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.allMatch(CrestPredicates.ge(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(CrestPredicates.allMatch(CrestPredicates.gt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("allMatch[<=[123]]", CrestPredicates.allMatch(CrestPredicates.le(123)).toString());
    }
  }

  public static class NoneMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.noneMatch(CrestPredicates.lt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(CrestPredicates.noneMatch(CrestPredicates.gt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("noneMatch[<=[123]]", CrestPredicates.noneMatch(CrestPredicates.le(123)).toString());
    }
  }

  public static class AnyMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.anyMatch(CrestPredicates.le(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(CrestPredicates.anyMatch(CrestPredicates.lt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("anyMatch[<=[123]]", CrestPredicates.anyMatch(CrestPredicates.le(123)).toString());
    }
  }

  public static class MatchesRegexTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.matchesRegex("hello.").test("hello!"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.matchesRegex(".ello.").test("hello"));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("matchesRegex[hello.]", CrestPredicates.matchesRegex("hello.").toString());
    }
  }

  public static class ContainsStringTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(CrestPredicates.containsString("hello").test("hello!"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(CrestPredicates.containsString(".ello.").test("hello!"));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("containsString[hello]", CrestPredicates.containsString("hello").toString());
    }
  }
}
