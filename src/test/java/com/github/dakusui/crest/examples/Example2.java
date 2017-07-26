package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.matcherbuilders.MatcherBuilders;
import com.github.dakusui.crest.predicates.CrestPredicates;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.github.dakusui.crest.matcherbuilders.MatcherBuilders.allOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

public class Example2 {
  @Test
  public void test() {
    Predicate<Integer> predicate = CrestPredicates.eq(100).and(CrestPredicates.eq(200));
    System.out.println(predicate);
    System.out.println(predicate.test(123));
  }

  @Test
  public void test3() {
    assertThat(
        asList("Hello", "world").toString(),
        allOf(
            MatcherBuilders.asString().containsString("Hello").matcher(),
            MatcherBuilders.asString().matchesRegex("HELLO").containsString("WORLD").any()
        )
    );
  }

  @Test
  public void test4() {
    assertThat(
        Arrays.asList("Hello", "world"),
        MatcherBuilders.allOf(
            MatcherBuilders.asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            MatcherBuilders.asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            MatcherBuilders.asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            MatcherBuilders.asString().containsString("bye").matcher()
        )
    );
  }

  @Test
  public void test5() {
    assertThat(
        "Gallia est omnis divisa quarun unum incolunt Belgae",
        MatcherBuilders.allOf(
            MatcherBuilders.asString().check("contains", "est").matcher()
        )
    );
  }

}
