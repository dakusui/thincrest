package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.CrestMatchers;
import com.github.dakusui.crest.CrestPredicates;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static com.github.dakusui.crest.CrestMatchers.allOf;
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
            Crest.asString().containsString("Hello").matcher(),
            Crest.asString().matchesRegex("HELLO").containsString("WORLD").any()
        )
    );
  }

  @Test
  public void test4() {
    assertThat(
        Arrays.asList("Hello", "world"),
        CrestMatchers.allOf(
            Crest.asStream().noneMatch("bye"::equals).matcher()
        )
    );
  }
}
