package com.github.dakusui;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.Formattable;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.dakusui.crest.CrestFunctions.elementAt;
import static com.github.dakusui.crest.CrestFunctions.size;
import static com.github.dakusui.crest.CrestMatchers.allOf;
import static com.github.dakusui.crest.CrestMatchers.anyOf;
import static com.github.dakusui.crest.CrestPredicates.eq;
import static com.github.dakusui.crest.CrestPredicates.equalTo;
import static org.junit.Assert.assertThat;

public class Example {

  private static final Predicate<Integer> FAILING_CHECK = Formattable.predicate("failingCheck", v -> {
    throw new RuntimeException("FAILED");
  });

  @Test
  public void whenSimpleFailingConj$thenFailsAndMessageAppropriate() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(2)).matcher(),
            Crest.<List<String>, Integer>create(size()).and(eq(10), eq(100)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void whenSimplePassingConj$thenPasses() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(3)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("Hello")).matcher()
        ));
  }


  @Test
  public void whenNestedFailingConj$thenFailsAndMessageAppropriate() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(2)).matcher(),
            Crest.<List<String>, Integer>create(size()).and(eq(10), eq(100)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test2() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        anyOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(2)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test3() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    Predicate<Integer> failingCheck = Formattable.predicate("failingCheck", v -> {
      throw new RuntimeException("FAILED");
    });
    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(2), failingCheck).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test4() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    Predicate<Integer> failingCheck = Formattable.predicate("failingCheck", v -> {
      throw new RuntimeException("FAILED");
    });
    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).and(equalTo(2), failingCheck).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test5() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).or(equalTo(3), FAILING_CHECK).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test6() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).or(FAILING_CHECK, equalTo(3)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }

  @Test
  public void test7() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>create(size()).or(FAILING_CHECK, equalTo(2)).matcher(),
            Crest.<List<String>, String>create(elementAt(0)).and(equalTo("hello")).matcher()
        ));
  }
}
