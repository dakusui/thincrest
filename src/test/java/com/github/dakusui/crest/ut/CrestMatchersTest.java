package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.junit.CrestUnit;
import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.matcherbuilders.AsObject;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.dakusui.crest.functions.CrestFunctions.elementAt;
import static com.github.dakusui.crest.functions.CrestFunctions.size;
import static com.github.dakusui.crest.CrestMatchers.allOf;
import static com.github.dakusui.crest.CrestMatchers.anyOf;
import static com.github.dakusui.crest.predicates.CrestPredicates.equalTo;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class CrestMatchersTest {

  private static final Predicate<Integer> FAILING_CHECK = Formattable.predicate("failingCheck", v -> {
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
   */
  public static class ConjTest extends CrestUnit {
    /**
     * <pre>
     *   Conj
     *   (1): P -> P      : pass
     * </pre>
     */
    @Test
    public void whenPassingAndThenPassing$thenPasses() {
      List<String> aList = composeTestData();

      Optional<Description> description = CrestMatchersTest.describeFailure(
          aList,
          allOf(
              Crest.<List<String>, Integer, Crest>asObject(
                  size()
              ).check(
                  equalTo(3)
              ).all(),
              Crest.<List<String>, Object, Crest>asObject(
                  elementAt(0)
              ).check(equalTo(
                  "Hello"
              )).all()
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
              Crest.<List<String>, Integer, AsObject>asObject(size())
                  .check(equalTo(3))
                  .all(),
              Crest
                  .<List<String>, Object, AsObject>asObject(elementAt(0))
                  .check(equalTo("hello"))
                  .all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: and:[\n"
              + "  equalTo[3](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
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
              Crest.<List<String>, Integer, AsObject>asObject(size()).check(FAILING_CHECK).all(),
              Crest.<List<String>, Object, AsObject>asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith(
              "\n" +
                  "Expected: and:[\n"
                  + "  failingCheck(size(x))\n"
                  + "  equalTo[Hello](elementAt[0](x))\n"
                  + "]->true\n"
                  + "     but: when x=<[Hello, world, !]>; then and:[\n"
                  + "  failingCheck(size(x)) failed with java.lang.RuntimeException(FAILED)\n"
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
              Crest.<List<String>, Object, Crest>asObject(size()).check(equalTo(2)).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: and:[\n"
              + "  equalTo[2](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3>\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
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
  public static class DisjTest extends CrestUnit {

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
              Crest.<List<String>, Integer, Crest>asObject(size()).check(equalTo(3)).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("Hello")).all()
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
              Crest.<List<String>, Integer, Crest>asObject(size()).check(equalTo(3)).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("hello")).all()
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
     */
    @Test
    public void whenErrorAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.<List<String>, Integer, Crest>asObject(size()).check(FAILING_CHECK).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith("\n" +
              "Expected: or:[\n"
              + "  failingCheck(size(x))\n"
              + "  equalTo[Hello](elementAt[0](x))\n"
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  failingCheck(size(x)) failed with java.lang.RuntimeException(FAILED)\n"
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
              Crest.<List<String>, Integer, Crest>asObject(size()).check(equalTo(2)).matcher(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("hello")).matcher()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n" +
              "Expected: or:[\n"
              + "  equalTo[2](size(x))\n"
              + "  equalTo[hello](elementAt[0](x))\n"
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3>\n"
              + "  equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  public static class NestedTest extends CrestUnit {
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
              Crest.<List<String>, Integer, Crest>asObject(size()).check(equalTo(2)).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).all()
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
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then or:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3>\n"
              + "  and:[\n"
              + "    equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
              + "    equalTo[HELLO](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
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
              Crest.<List<String>, Integer, Crest>asObject(size()).check(equalTo(2)).all(),
              Crest.<List<String>, Object, Crest>asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).any()
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
              + "]->true\n"
              + "     but: when x=<[Hello, world, !]>; then and:[\n"
              + "  equalTo[2](size(x)) was false because size(x)=<3>\n"
              + "  or:[\n"
              + "    equalTo[hello](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
              + "    equalTo[HELLO](elementAt[0](x)) was false because elementAt[0](x)=\"Hello\"\n"
              + "  ]->false\n"
              + "]->false",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  private static <T> Optional<Description> describeFailure(T actual, Matcher<? super T> matcher) {
    if (!matcher.matches(actual)) {
      Description description = new StringDescription();
      description
          .appendText("\nExpected: ")
          .appendDescriptionOf(matcher)
          .appendText("\n     but: ");
      matcher.describeMismatch(actual, description);

      return Optional.of(description);
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
