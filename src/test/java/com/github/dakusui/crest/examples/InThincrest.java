package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Formattable;
import com.github.dakusui.crest.matcherbuilders.Crest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import java.util.*;

import static com.github.dakusui.crest.functions.CrestFunctions.size;
import static com.github.dakusui.crest.functions.CrestPredicates.isEmpty;
import static com.github.dakusui.crest.matcherbuilders.Crest.*;

/**
 * http://qiita.com/disc99/items/31fa7abb724f63602dc9
 */
public class InThincrest {
  private final List<String>       aList       = Collections.unmodifiableList(Arrays.asList("hoge", "fuga", "piyo"));
  private final String             aString     = "Hello, \tworld";
  private final String[]           anArray     = { "Gallia", "est", "omnis", "divisa" };
  private final Collection<String> aCollection = Collections.unmodifiableCollection(aList);
  private final Iterator<String>   anIterator  = aList.iterator();

  @Test
  public void withThincrest2$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(0).matcher(),
            Crest.asList().containsAll(Arrays.asList("hoge", "fuga", "piyo", "poyo")).matcher()
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
            asList().isInstanceOf(LinkedList.class).matcher(),
            asList().isSameAs(Collections.emptyList()).matcher()
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
  public void qiita_16$thenFail() {
    Crest.assertThat(
        aString,
        asString(
            Formattable.function("trimSpace", (String s) -> s.replaceAll("\\s", ""))
        ).equalsIgnoreCase("HELLO,WORLD!").matcher()
    );
  }

  @Test
  public void qiita_16_simpler$thenFail() {
    Crest.assertThat(
        aString,
        asString((String s) -> s.replaceAll("\\s", "")).equalsIgnoreCase("HELLO,WORLD!").matcher()
    );
  }

  @Test
  public void qiita_20$thenFail() {
    Crest.assertThat(
        aString,
        asString().matchesRegex("[0-9]+").matcher()
    );
  }

  @Test
  public void qiita_20_another$thenFail() {
    Crest.assertThat(
        aString,
        asString().check(Formattable.predicate("containsOnlyDigits", s -> s.matches("[0-9]+"))).matcher()
    );
  }

  @Test
  public void qiita_21$thenFail() {
    Crest.assertThat(
        aString,
        asInteger(
            Formattable.function("countLines", (String s) -> s.split("\n").length)
        ).eq(30).matcher()
    );
  }

  @Test
  public void qiita_21_simpler$thenFail() {
    // or more simply, if you don't need friendly method explanation on failure.

    Crest.assertThat(
        aString,
        asInteger((String s) -> s.split("\n").length).eq(30).matcher()
    );
  }

  @Test
  public void qiita_24_25_26_27_integer$thenFail() {
    Crest.assertThat(
        123,
        allOf(
            asInteger().gt(100).matcher(),
            asInteger().ge(100).matcher(),
            asInteger().lt(200).matcher(),
            asInteger().le(200).matcher()
        )
    );
  }

  @Test
  public void qiita_29_30_32$thenFail() {

  }

  @Test
  public void qiita_31_32$thenFail() {
    Crest.assertThat(
        aCollection,
        allOf(
            asList().isEmpty().matcher(),
            asInteger(size()).equalTo(2).matcher()
        )
    );
  }

  @Test
  public void qiita_33$thenFail() {
    Crest.assertThat(
        anIterator,
        // To check if its empty or not, type doesn't matter. Let's say 'Object'.
        Crest.asList((Iterator i) -> new LinkedList<Object>() {{
          while (i.hasNext()) {
            add(i.next());
          }
        }}).isEmpty().matcher()
    );
  }

  @Test
  public void qiita_42$thenFail() {
    Crest.assertThat(
        anArray,
        // To check if its empty or not, type doesn't matter. Let's say 'Object'.
        Crest.asList(Arrays::asList).isEmpty().matcher()
    );
  }

  @Test
  public void qiita_40_43$thenFail() {
    Crest.assertThat(
        anArray,
        allOf(
            // Not type safe if you don't give type
            Crest.asList(Arrays::asList).containsAll(Arrays.asList("Hello", 1)).matcher(),
            // A safer way. <I, E> I for input, E for type of elements in output.
            Crest.<String[], String>asList(Arrays::asList).contains("Hello").matcher()
        ));
  }

  @Test
  public void hamcrestSandbox() {
    assertThat("Hello, world", Matchers.stringContainsInOrder("Hello", "world"));
    assertThat("Hello, world", Matchers.stringContainsInOrder("world", "Hello"));
    Matchers.equalToIgnoringWhiteSpace("");

    Matchers.stringContainsInOrder();

    Matchers.empty();
  }


}
