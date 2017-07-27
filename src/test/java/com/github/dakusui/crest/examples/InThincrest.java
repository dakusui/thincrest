package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.matcherbuilders.Crest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.crest.functions.CrestFunctions.size;
import static com.github.dakusui.crest.functions.CrestPredicates.isEmpty;
import static com.github.dakusui.crest.matcherbuilders.Crest.*;

/**
 * http://qiita.com/disc99/items/31fa7abb724f63602dc9
 */
public class InThincrest {
  List<String> aList = Arrays.asList("hoge", "fuga", "piyo");

  @Test
  public void withThincrest2$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(0).matcher(),
            Crest.asListOf(String.class).containsAll(Arrays.asList("hoge", "fuga", "piyo", "poyo")).matcher()
        )
    );
  }

  // (2)
  // (3)
  @Test
  public void qiita_2_3$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asBoolean(isEmpty()).isTrue().matcher(),
            asBoolean(isEmpty()).isFalse().matcher()
        )
    );
  }

  @Test
  public void qiita_4_5$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asObject().equalTo("").matcher(),
            asObject().equalTo(aList).matcher()
        )
    );
  }

  // (6)
  // (7)
  @Test
  public void qiita_6_7$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asObject().isNull().matcher(),
            asObject().isNotNull().matcher()
        )
    );
  }

  // (8)
  @Test
  public void qiita_8$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(3).matcher(),
            asComparableOf(String.class, "toString").eq("toString").matcher()
        )
    );
  }

  @Test
  public void qiita_9_10$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asListOf(String.class).isInstanceOf(LinkedList.class).matcher(),
            asListOf(String.class).isSameAs(Collections.emptyList()).matcher()
        )
    );
    CoreMatchers.containsString("");
    StringContains.containsString("");
    StringContains.containsString("");
  }

  @Test
  public void qiita_11$thenFail() {
    Crest.assertThat(
        aList,
        asString().equalTo("[hello, world]").matcher()
    );
  }

  @Test
  public void qiita_12_13_14_15$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asString().startsWith("[HELLO").matcher(),
            asString().endsWith("WORLD]").matcher(),
            asString().containsString("***").matcher(),
            asString().equalsIgnoreCase("***").matcher()
        )
    );
  }

  @Test
  public void qiita_12_13_14_15_17_18_19$thenPass() {
    Crest.assertThat(
        aList,
        anyOf(
            asString().startsWith("[hoge").matcher(),
            asString().endsWith("WORLD]").matcher(),
            asString().containsString("***").matcher(),
            asString().equalsIgnoreCase("***").matcher(),
            asString().isEmpty().matcher(),
            asString().isEmptyOrNull().matcher(),
            asString().matchesRegex(".*hoge.*").matcher()
        )
    );
  }

  @Test
  public void qiita_24_25_26_28$thenFail() {

  }

  @Test
  public void qiita_29_30_32$thenFail() {

  }

  @Test
  public void hamcrestSandbox() {
    assertThat("Hello, world", Matchers.stringContainsInOrder("Hello", "world"));
    assertThat("Hello, world", Matchers.stringContainsInOrder("world", "Hello"));
    Matchers.equalToIgnoringWhiteSpace("");

    Matchers.stringContainsInOrder();
  }

}
