package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.utils.printable.Predicates;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;

public class Issue27Test {
  @Test
  public void givenTransformingPredicateCreatedByCall$whenFails$thenPrintedPretty() {
    Matcher matcher = allOf(asString("toLowerCase").check(
        call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
        equalTo('z')
    ).$());
    Assertion<String> assertion = new Assertion.Impl<>("FAILED", matcher);

    matcher.<String>describeExpectation(assertion).forEach(System.out::println);
    matcher.<String>describeMismatch("WORLD", assertion).forEach(System.err::println);
  }

  @Test
  public void test() {
    assertThat(
        "WORLD",
        allOf(
        asString("toLowerCase").check(
            call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
            equalTo('z')
        ).$())
    );
  }
}