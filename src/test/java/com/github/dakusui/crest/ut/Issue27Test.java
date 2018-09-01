package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.Assertion;
import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.crest.utils.printable.Predicates;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.crest.Crest.call;

public class Issue27Test {
  @Test
  public void givenTransformingPredicateCreatedByCall$whenFails$thenPrintedPretty() {
    Matcher matcher = Crest.asString().check(
        call("toUpperCase").andThen("substring", 2).andThen("contains", "X").$(),
        Predicates.equalTo("world")
    ).$();
    Assertion<String> assertion = new Assertion.Impl<>("FAILED", matcher);

    List<String> expectation = matcher.<String>describeExpectation(assertion);
    List<String> mismatch = matcher.<String>describeMismatch("WORLD", assertion);

    expectation.forEach(System.out::println);
    mismatch.forEach(System.err::println);
  }
}
