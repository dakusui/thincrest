package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.junit.Test;

import java.util.Arrays;

import static com.github.dakusui.crest.matcherbuilders.Crest.*;
import static java.util.Arrays.asList;

public class Example2 {
  @Test
  public void test3() {
    assertThat(
        asList("Hello", "world").toString(),
        allOf(
            asString().containsString("Hello").matcher(),
            asString().matchesRegex("HELLO").containsString("WORLD").any()
        )
    );
  }

  @Test
  public void test4() {
    assertThat(
        Arrays.asList("Hello", "world"),
        allOf(
            asString().containsString("hello").matcher(),
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asString().containsString("bye").matcher()
        )
    );
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test5() {
    assertThat(
        "Gallia est omnis divisa in partes tres, quarun unum incolunt Belgae, "
            + "alium Aquitani, tertium linua ipsorum Celtae, nostra Galli appelantur",
        allOf(
            asString().check("contains", "est").containsString("Caesar").matcher(),
            Crest.asObject("length").check(Formattable.predicate(">1024", o -> ((Integer) o) > 1024)).matcher()
        )
    );
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test6() {
    assertThat(
        "",
        allOf(
            asObject("length").check("equals", "hello").matcher()
        )
    );
  }

  @Test
  public void test7() {
    assertThat(
        "Hello, Crest's world",
        "--actual string--",
        allOf(
            asComparable(CrestFunctions.length()).ge(5).lt(50).matcher(),
            Crest.<String, Integer>asComparable("length").gt(50).matcher()
        )
    );
  }

}
