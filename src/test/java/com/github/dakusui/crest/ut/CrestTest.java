package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.InternalUtils;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.allOf;
import static com.github.dakusui.crest.Crest.anyOf;
import static com.github.dakusui.crest.utils.printable.Functions.elementAt;
import static com.github.dakusui.crest.utils.printable.Functions.size;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class CrestTest {
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
              + "  size(x) equalTo[3]\n"
              + "  elementAt[0](x) equalTo[hello]\n"
              + "]\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
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
                  + "  size(x) failingCheck\n"
                  + "  elementAt[0](x) equalTo[Hello]\n"
                  + "]\n"
                  + "     but: when x=<[Hello, world, !]>; then and:[\n"
                  + "  size(x) failingCheck failed with java.lang.RuntimeException(FAILED)\n"
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
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=<3>\n"
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
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
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
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=<3>\n"
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
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=<3>\n"
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
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  size(x) equalTo[2] was not met because size(x)=<3>\n"
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

  public static class NegativesTest extends TestBase {
    @Test
    public void given_NotMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.not(
              Crest.asString().containsString("HELLO").$()
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
              Crest.asString().containsString("WORLD").$()
          )
      );
      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }


    @Test
    public void given_NoneOfMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.noneOf(
              Crest.asString().eq("WORLD").$(),
              Crest.asString().containsString("HELLO").$()
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
              Crest.asString().eq("WORLD").$(),
              Crest.asString().containsString("hellox").$()
          )
      );

      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }

  }
}
