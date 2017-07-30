package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.functions.CrestFunctions;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static com.github.dakusui.crest.matcherbuilders.Crest.*;

public class SimpleExamples {
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
        asString().matchesRegex(".*Hello.*").all()
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
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnlyString_Hello_$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asList().containsOnly(Collections.singletonList("Hello")).matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnlyString_Hello_World_$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asList().containsOnly(Arrays.asList("Hello", "world")).matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnly_Hello_world_everyone_$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        asList().containsOnly(Arrays.asList("Hello", "world", "everyone")).matcher()
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
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void test8_b$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject("toString").equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject().equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8a$thenFail() {
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
            asString().containsString("Caesar").check("contains", "est").containsString("Caesar").matcher(),
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
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndFailingCheck$thenFail() {
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
  public void given_50_$whenAsComparableAndDoFailingCheck$thenFail() {
    assertThat(
        50,
        Crest.asComparableOf(Integer.class).ge(5).lt(50).all()
    );
  }


  @Test
  public void givenList$$whenContains101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.asList().contains(101).matcher()
    );
  }

  @Test
  public void givenList$$whenContains100$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.asList().contains(100).matcher()
    );
  }


  @Test
  public void givenList$$whenContainsAll101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.asList().containsAll(Arrays.asList(100, 101)).matcher()
    );
  }

  @Test
  public void givenList$$whenContainsAll100and200$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.asList().containsAll(Arrays.asList(100, 200)).matcher()
    );
  }

  @Test
  public void givenList$$whenIsEmpty$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.asList().contains("100").isEmpty().matcher()
    );
  }

  @Test
  public void givenTypedList$$whenIsEmpty$thenFail() {
    List<Integer> aList = Arrays.asList(100, 200, 300, 400, 500);
    assertThat(
        aList,
        Crest.asList().contains("100").isEmpty().matcher()
    );
  }

  @Test
  public void givenEmptyList$$whenIsEmpty$thenPass() {
    assertThat(
        Collections.emptyList(),
        Crest.asList().isEmpty().matcher()
    );
  }

  @Test
  public void givenString$whenParseIntAndTest$thenPass() {
    assertThat(
        "123",
        asInteger("length").eq(3).matcher()
    );
  }

  @Test
  public void givenArray$whenHasKey$thenFail() {
    Object[][] in = {
        { "hello", 5 },
        { "world", 5 },
        { "everyone", 8 },
    };
    Function<Object[][], HashMap<Object, Object>> arrToMap = Formattable.function(
        "arrToMap",
        (Object[][] arr) -> new HashMap<Object, Object>() {
          {
            for (Object[] each : arr)
              put(each[0], each[1]);
          }
        }
    );
    assertThat(
        in,
        Crest.asMap(arrToMap).hasKey("").hasKey(200).matcher()
    );
  }

  @Test
  public void givenArray$whenHasKey$thenPass() {
    Object[][] in = {
        { "hello", 5 },
        { "world", 5 },
        { "everyone", 8 },
    };
    Function<Object[][], HashMap<Object, Object>> arrToMap = Formattable.function(
        "arrToMap",
        (Object[][] arr) -> new HashMap<Object, Object>() {
          {
            for (Object[] each : arr)
              put(each[0], each[1]);
          }
        }
    );
    assertThat(
        in,
        Crest.asMap(arrToMap).hasKey("hello").hasKey("world").matcher()
    );
  }

  @Test
  public void givenMapWithoutTypes$whenHasKey$thenFail() {
    Map map = new HashMap<Object, Object>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asMap().hasKey("").hasKey(200).matcher()
        )
    );
  }


  @Test
  public void givenMap$whenHasValue$thenPass() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        Crest.asMapOf(String.class, Integer.class).hasValue(5).matcher()
    );
  }

  @Test
  public void givenMap$whenHasValue$thenFail() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asMapOf(String.class, Integer.class).hasValue(10).matcher()
        )
    );
  }

  @Test
  public void givenMap$whenHasEntry$thenPass() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        Crest.asMapOf(String.class, Integer.class).hasEntry("world", 5).matcher()
    );
  }

  @Test
  public void givenMap$whenHasEntry$thenFail() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asMapOf(String.class, Integer.class).hasEntry("hello", 8).matcher()
        )
    );
  }

  @Test
  public void givenTrue$whenAsBooleanAndCheck$thenFail() {
    assertThat(
        true,
        asBoolean().isFalse().matcher()
    );
  }

  @Test
  public void givenTrue$whenAsBooleanAndCheck$thenPass() {
    assertThat(
        true,
        asBoolean().isTrue().matcher()
    );
  }

  @Test
  public void given_true_$whenAsBooleanAndCheck$thenFail() {
    assertThat(
        "true",
        asBoolean("equals", "hello").isTrue().matcher()
    );
  }

  @Test
  public void given_true_$whenAsBooleanAndCheck$thenPass() {
    assertThat(
        "true",
        asBoolean("equals", "true").isTrue().matcher()
    );
  }
}
