package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.github.dakusui.crest.matcherbuilders.Crest.*;

public class Example2 {
  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfMatchesRegex_HELLO_$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asString().matchesRegex("HELLO").any()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfMatchesRegexWithMathingOne$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world").toString(),
        asString().matchesRegex("Hello").all()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfContainsString_BYE_$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asString().containsString("BYE").matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfContainsString_Hello_$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asString().containsString("Hello").matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringWithDynamic_toUpperCase_andContainsString_hello$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world").toString(),
        asString("toUpperCase").containsString("hello!").matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringWithDynamic_toUpperCase_andContainsString_HELLO_$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world").toString(),
        asString("toUpperCase").containsString("HELLO").matcher()
    );
  }

  @Test
  public void given100$whenAsComparableOfIntegerAndThenEq_100_$thenPass() {
    assertThat(
        100,
        asComparableOf(Integer.class).eq(100).matcher()
    );
  }


  @Test
  public void given_abc_$whenAsComparableOfStringAndThenEq_ABC_$thenFail() {
    assertThat(
        "abc",
        asComparableOf(String.class).eq("ABC").matcher()
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndPassingConditionComposedBy_allOf_method$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        allOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher()
        )
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndFailingConditionComposedBy_allOf_method$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        allOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndPassingConditionComposedBy_anyOf_method$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        anyOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndFailingConditionComposedBy_anyOf_method$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        anyOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void test8_b() {
    assertThat(
        "aStringToBeExamined",
        //        anyOf(
        asObject().<String>equalTo("aStringToBeExamined!").matcher()
        //            asObjectOf(String.class).equalTo("aStringToBeExamined").matcher(),
        //            asComparableOf(Integer.class, "length").eq(0).matcher()
        //        )
    );
  }

  @Test
  public void test8() {
    assertThat(
        "aStringToBeExamined",
        //        anyOf(
        asObject().equalTo("aStringToBeExamined!").matcher()
        //            asObjectOf(String.class).equalTo("aStringToBeExamined").matcher(),
        //            asComparableOf(Integer.class, "length").eq(0).matcher()
        //        )
    );
  }

  @Test
  public void test8a() {
    assertThat(
        "aStringToBeExamined",
        asObject().equalTo("aStringToBeExamined2").all()
    );

    assertThat(
        "aStringToBeExamined",
        asObject().all()
    );

    assertThat(
        "aStringToBeExamined",
        asString().equalTo("aStringToBeExamined2").all()
    );
  }


  @Test
  public void given_DeBelloGallicco_$whenAllOfAsStringFailingAndAsObjectFailingMatchers$thenFail() {
    assertThat(
        "Gallia est omnis divisa in partes tres, quarun unum incolunt Belgae, "
            + "alium Aquitani, tertium linua ipsorum Celtae, nostra Galli appelantur",
        allOf(
            asString().check("contains", "est").containsString("Caesar").matcher(),
            asObject("length").check(Formattable.predicate(">1024", o -> ((Integer) o) > 1024)).matcher()
        )
    );
  }

  @Test
  public void givenEmptyString$whenAsObjectByMethodCallAndDoFailingCheckByMethodCall$thenFail() {
    assertThat(
        "",
        asObject("length").check("equals", "hello").matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndFailingCheck$thenFailing() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").gt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").lt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByPresetLengthFunctionAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparable(CrestFunctions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_Hello_world_$whenAsComparableAndDoPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        asComparable(CrestFunctions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_50_$whenAsComparableAndDoFailingCheck$thenFailing() {
    assertThat(
        50,
        Crest.asComparableOf(Integer.class).ge(5).lt(50).all()
    );
  }


  @Test
  public void givenList$$whenContains101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().contains(101).matcher()
    );
  }

  @Test
  public void givenList$$whenContains100$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().contains(100).matcher()
    );
  }


  @Test
  public void givenList$$whenContainsAll101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().containsAll(Arrays.asList(100, 101)).matcher()
    );
  }

  @Test
  public void givenList$$whenContainsAll100and200$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().containsAll(Arrays.asList(100, 200)).matcher()
    );
  }

  @Test
  public void givenList$$whenIsEmpty$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().isEmpty().matcher()
    );
  }

  @Test
  public void givenEmptyList$$whenIsEmpty$thenPass() {
    assertThat(
        Collections.emptyList(),
        Crest.asList().isEmpty().matcher()
    );
  }

}
