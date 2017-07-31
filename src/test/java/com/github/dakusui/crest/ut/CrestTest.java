package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.core.Printable;
import com.github.dakusui.crest.matcherbuilders.Crest;
import com.github.dakusui.crest.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.dakusui.crest.functions.CrestFunctions.elementAt;
import static com.github.dakusui.crest.functions.CrestFunctions.size;
import static com.github.dakusui.crest.functions.CrestPredicates.equalTo;
import static com.github.dakusui.crest.matcherbuilders.Crest.allOf;
import static com.github.dakusui.crest.matcherbuilders.Crest.anyOf;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class CrestTest {
  static class Description {
    private final String content;

    public Description(String s) {
      this.content = s;
    }

    @Override
    public String toString() {
      return this.content;
    }
  }

  private static final Predicate<Integer> FAILING_CHECK = Printable.predicate("failingCheck", v -> {
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
              + "  equalTo[3](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[3](size(x))\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
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
    public void whenErrorAndThenPassing$thenErrorThrownAndMessageAppropriate() {
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
                  + "  failingCheck(size(x))\n"
                  + "  equalTo[Hello](elementAt[0](x))\n"
                  + "]\n"
                  + "     but: when x=<[Hello, world, !]>; then and:[\n"
                  + "  failingCheck(size(x)) failed with java.lang.RuntimeException(FAILED)\n"
                  + "  equalTo[Hello](elementAt[0](x))\n"
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
              + "  equalTo[2](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3> does not satisfy it\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
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
              + "  failingCheck(size(x))\n"
              + "  equalTo[Hello](elementAt[0](x))\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  failingCheck(size(x)) failed with java.lang.RuntimeException(FAILED)\n"
              + "  equalTo[Hello](elementAt[0](x))\n"
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
              + "  equalTo[2](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3> does not satisfy it\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
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
              + "  equalTo[2](size(x))\n"
              + "  and:[\n"
              + "    equalTo[hello](elementAt[0](x))\n"
              + "    equalTo[HELLO](elementAt[0](x))\n"
              + "  ]\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3> does not satisfy it\n"
              + "  and:[\n"
              + "    equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
              + "    equalTo[HELLO](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
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
              + "  equalTo[2](size(x))\n"
              + "  or:[\n"
              + "    equalTo[hello](elementAt[0](x))\n"
              + "    equalTo[HELLO](elementAt[0](x))\n"
              + "  ]\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3> does not satisfy it\n"
              + "  or:[\n"
              + "    equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
              + "    equalTo[HELLO](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\" does not satisfy it\n"
              + "  ]->false\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  private static <T> Optional<Description> describeFailure(T actual, Matcher<? super T> matcher) {
    Assertion<T> assertion = Assertion.create(null, matcher);
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
}
