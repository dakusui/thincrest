package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.*;
import com.github.dakusui.crest.utils.TestBase;
import com.github.dakusui.crest.utils.printable.Predicates;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.AssumptionViolatedException;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Functions.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class CrestTest {
  public static <T> Assertion<T> create(String messageOnFailure, Matcher<? super T> matcher) {
    return new Assertion.Impl<>(messageOnFailure, matcher);
  }

  static class Description {
    private final String content;

    Description(String s) {
      this.content = s;
    }

    @Override
    public String toString() {
      return this.content;
    }
  }

  private static final Predicate<Integer> FAILING_CHECK = InternalUtils.predicate("failingCheck", v -> {
    throw new RuntimeException("FAILED");
  });

  private static final Function<List<String>, Integer> FAILING_TRANSFORM = InternalUtils.function("failingTransform", v -> {
    throw new RuntimeException("FAILED");
  });

  /**
   * <pre>
   *   Conj
   *   (1): P -> P      : pass
   *   (2): P -> F      : fail
   *   (3): E -> P      : fail
   *   (4): F -> F      : fail
   * </pre>
   * <pre>
   *   TestData: ["Hello", "world", "!"]
   * </pre>
   */
  public static class ConjTest extends TestBase {
    /**
     * <pre>
     *   Conj
     *   (1): P -> P      : pass
     * </pre>
     */
    @Test
    public void whenPassingAndThenPassing$thenPasses() {
      List<String> aList = composeTestData();

      Optional<Description> description = CrestTest.describeFailure(
          aList,
          allOf(
              Crest.asObject(
                  elementAt(0)
              ).check(
                  equalTo("Hello")).all()
              ,
              Crest.asObject(
                  size()
              ).check(
                  equalTo(3)
              ).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }

    @Test
    public void makeSureCalledOnlyOnce() {
      List<String> aList = composeTestData();

      Optional<Description> description = CrestTest.describeFailure(
          aList,
          allOf(
              Crest.asObject(
                  new Function<List<?>, String>() {
                    boolean firstTime = true;

                    @Override
                    public String apply(List<?> objects) {
                      try {
                        if (firstTime)
                          return (String) elementAt(0).apply(objects);
                        else
                          throw new Error();
                      } finally {
                        firstTime = false;
                      }
                    }
                  }
              ).check(
                  new Predicate<String>() {
                    boolean firstTime = true;

                    @Override
                    public boolean test(String s) {
                      try {
                        if (firstTime)
                          return equalTo("Hello").test(s);
                        else
                          throw new Error();
                      } finally {
                        firstTime = false;
                      }
                    }
                  }
              ).all()
              ,
              Crest.asObject(
                  size()
              ).check(
                  equalTo(3)
              ).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }


    /**
     * <pre>
     *   Conj
     *   (2): P -> F      : fail
     * </pre>
     */
    @Test
    public void whenPassingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size())
                  .check(equalTo(3))
                  .all(),
              Crest.asObject(elementAt(0))
                  .check(equalTo("hello"))
                  .all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: and:[\n"
              + "  size(x) equalTo[3]\n"
              + "  elementAt[0](x) equalTo[hello]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then and:[\n"
              + "  size(x) equalTo[3]\n"
              + "  elementAt[0](x) equalTo[hello] was not met because elementAt[0](x)=\"Hello\"\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }

    /**
     * <pre>
     *   Conj
     *   (3): E -> P      : error
     * </pre>
     */
    @Test
    public void whenErrorOnCheckAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(FAILING_CHECK).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith(
              "\n" +
                  "Expected: and:[\n"
                  + "  size(x) failingCheck\n"
                  + "  elementAt[0](x) equalTo[Hello]\n"
                  + "]\n"
                  + "     but: when x=[[Hello, world, !]]; then and:[\n"
                  + "  size(x) failingCheck failed with java.lang.RuntimeException(FAILED)\n"
                  + "  elementAt[0](x) equalTo[Hello]\n"
                  + "]->false\n"
                  + "FAILED"
          ));
    }

    /**
     * <pre>
     *   Conj
     *   (3): E -> P      : error
     * </pre>
     */
    @Test
    public void whenErrorOnTransformAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(FAILING_TRANSFORM).check(Predicates.alwaysTrue()).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith(
              "\n" +
                  "Expected: and:[\n"
                  + "  failingTransform(x) alwaysTrue\n"
                  + "  elementAt[0](x) equalTo[Hello]\n"
                  + "]\n"
                  + "     but: when x=[[Hello, world, !]]; then and:[\n"
                  + "  failingTransform(x) alwaysTrue failed with java.lang.RuntimeException(FAILED)\n"
                  + "  elementAt[0](x) equalTo[Hello]\n"
                  + "]->false\n"
                  + "FAILED"
          ));
    }

    /**
     * <pre>
     *   Conj
     *   (4): F -> F      : fail
     * </pre>
     */
    @Test
    public void whenFailingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: and:[\n"
              + "  size(x) equalTo[2]\n"
              + "  elementAt[0](x) equalTo[hello]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then and:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=[3]\n"
              + "  elementAt[0](x) equalTo[hello] was not met because elementAt[0](x)=\"Hello\"\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  /**
   * <pre>
   *   Disj
   *   (1): P -> P      : pass
   *   (2): P -> F      : pass
   *   (3): E -> P      : fail
   *   (4): F -> F      : fail
   * </pre>
   */
  public static class DisjTest extends TestBase {

    /**
     * <pre>
     *   Disj
     *   (1): P -> P      : pass
     * </pre>
     */
    @Test
    public void whenPassingAndThen$thenPasses() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(3)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }


    /**
     * <pre>
     *   Disj
     *   (2): P -> F      : fail
     * </pre>
     */
    @Test
    public void whenDisjPassingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(3)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).all()
          ));

      System.out.println(description.orElse(null));
      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }

    /**
     * <pre>
     *   Disj
     *   (3): E -> P      : error
     * </pre>
     * In case an error is thrown, the assertion should fail even if all the other matchers are passing.
     */
    @Test
    public void whenErrorAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(FAILING_CHECK).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith("\n" +
              "Expected: or:[\n"
              + "  size(x) failingCheck\n"
              + "  elementAt[0](x) equalTo[Hello]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then or:[\n"
              + "  size(x) failingCheck failed with java.lang.RuntimeException(FAILED)\n"
              + "  elementAt[0](x) equalTo[Hello]\n"
              + "]->false\n"
              + "FAILED"
          )
      );
    }

    /**
     * <pre>
     *   Disj
     *   (4): F -> F      : fail
     * </pre>
     */
    @Test
    public void whenFailingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(2)).matcher(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).matcher()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: or:[\n"
              + "  size(x) equalTo[2]\n"
              + "  elementAt[0](x) equalTo[hello]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then or:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=[3]\n"
              + "  elementAt[0](x) equalTo[hello] was not met because elementAt[0](x)=\"Hello\"\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  public static class NestedTest extends TestBase {
    /**
     * <pre>
     *   Disj
     *     ->Conj
     * </pre>
     */
    @Test
    public void whenConjUnderDisj$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: or:[\n"
              + "  size(x) equalTo[2]\n"
              + "  and:[\n"
              + "    elementAt[0](x) equalTo[hello]\n"
              + "    elementAt[0](x) equalTo[HELLO]\n"
              + "  ]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then or:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=[3]\n"
              + "  and:[\n"
              + "    elementAt[0](x) equalTo[hello] was not met because elementAt[0](x)=\"Hello\"\n"
              + "    elementAt[0](x) equalTo[HELLO] was not met because elementAt[0](x)=\"Hello\"\n"
              + "  ]->false\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }

    /**
     * <pre>
     *   Conj
     *     ->Disj
     * </pre>
     */
    @Test
    public void whenDisjUnderConj$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).any()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: and:[\n"
              + "  size(x) equalTo[2]\n"
              + "  or:[\n"
              + "    elementAt[0](x) equalTo[hello]\n"
              + "    elementAt[0](x) equalTo[HELLO]\n"
              + "  ]\n"
              + "]\n"
              + "     but: when x=[[Hello, world, !]]; then and:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=[3]\n"
              + "  or:[\n"
              + "    elementAt[0](x) equalTo[hello] was not met because elementAt[0](x)=\"Hello\"\n"
              + "    elementAt[0](x) equalTo[HELLO] was not met because elementAt[0](x)=\"Hello\"\n"
              + "  ]->false\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  private static <T> Optional<Description> describeFailure(T actual, Matcher<? super T> matcher) {
    Assertion<T> assertion = create(null, matcher);
    if (!matcher.matches(actual, assertion)) {
      String description = "\nExpected: " +
          String.join("\n", matcher.describeExpectation(assertion)) +
          "\n     but: " +
          String.join("\n", matcher.describeMismatch(actual, assertion));

      return Optional.of(new Description(description));
    }
    return Optional.empty();
  }

  private static List<String> composeTestData() {
    return new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};
  }

  public static class NegativesTest extends TestBase {
    @Test
    public void given_NotMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.not(
              asString().containsString("HELLO").$()
          )
      );
      System.out.println(description.orElseThrow(RuntimeException::new));
      assertThat(
          description.<String>get().content,
          Matchers.<String>containsString("not:[\n"
              + "  toString(x) containsString[HELLO]\n"
              + "]->false")
      );
    }


    @Test
    public void given_NotMatcher_$whenPassingTestPerformed$thenPassed() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.not(
              asString().containsString("WORLD").$()
          )
      );
      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }


    @Test
    public void given_NoneOfMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.noneOf(
              asString().eq("WORLD").$(),
              asString().containsString("HELLO").$()
          )
      );
      System.out.println(description.orElseThrow(RuntimeException::new));
      assertThat(
          description.<String>get().content,
          Matchers.<String>containsString("toString(x) =[WORLD] was not met because toString(x)=\"HELLO\"")
      );
    }

    @Test
    public void given_NoneOfMatcher_$whenPassingTestPerformed$thenPasses() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.noneOf(
              asString().eq("WORLD").$(),
              asString().containsString("hellox").$()
          )
      );

      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }
  }

  public static class AssertAssumeRequireTest {
    @Test
    public void givenAssertThat$whenPasses$thenOk() {
      Crest.assertThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = ComparisonFailure.class)
    public void givenAssertThat$whenFailOnComparison$thenComparisonFailureThrown() {
      Crest.assertThat(
          "Check 'hello'",
          "hello",
          asString().equalTo("HELLO").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenAssertThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.assertThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }

    @Test
    public void givenRequireThat$whenPasses$thenOk() {
      Crest.requireThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenRequireThat$whenFailOnComparison$thenExecutionFalureThrown() {
      Crest.requireThat(
          "Check 'hello'",
          "hello",
          asString().equalTo("HELLO").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenRequireThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.requireThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }

    @Test
    public void givenAssumeThat$whenPasses$thenOk() {
      Crest.assumeThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = IOException.class)
    public void givenAssumeThat$whenFailOnComparison$thenExecutionFalureThrown() throws IOException {
      try {
        Crest.assumeThat(
            "Check 'hello'",
            "hello",
            asString().equalTo("HELLO").$()
        );
      } catch (AssumptionViolatedException e) {
        // Wrap with IOException, which cannot happen in this test procedure to
        // make sure intended exception (AssumptionViolatedException) is really
        // thrown.
        throw new IOException(e);
      }
    }

    @Test(expected = ExecutionFailure.class)
    public void givenAssumeThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.assumeThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }
  }

  public static class CallMechanismTest extends TestBase {
    @Ignore
    @Test
    public void givenStaticCall$whenToString$thenWorksRight() {
      Object func = call(Stream.class, "of", varargsOf(Integer.class, 1, 2, 3)).andThen("collect", Collectors.toList()).$();
      System.out.println(func.toString());
      try {
        Crest.assertThat(
            func,
            allOf(
                Crest.asString("toString").startsWith("@Stream.of[Integer:varargs[1, 2, 3]]->@collect[CollectorImpl@").$(),
                Crest.asInteger(call("apply", "NOTHING").andThen("size").$()).equalTo(1).$()
            )
        );
      } catch (ExecutionFailure e) {
        System.err.println(e.getMessage());
        throw e;
      }
    }

    @Test
    public void givenStaticCall2$whenToString$thenWorksRight() {
      Function<Object, String> func = call(String.class, "format", "<me=%s, %s>", varargs(THIS, "hello")).$();

      System.out.println(func.toString());
      System.out.println(func.apply("world"));
      Crest.assertThat(
          func,
          allOf(
              Crest.asString("toString").equalTo("@String.format[<me=%s, %s>, Object:varargs[(THIS), hello]]").$(),
              Crest.asString(call("apply", "world").$()).equalTo("<me=world, hello>").$()
          )
      );
    }

    @Test
    public void printExample() {
      Function func = Call.create("append", "hello").andThen("append", 1).andThen("append", "everyone").andThen("toString").$();
      System.out.println(func.toString());
      Function<StringBuilder, String> func2 = (StringBuilder b) -> b.append("hello").append(1).append("world").append("everyone").toString();
      System.out.println(func2.toString());
    }

  }
}
