package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.utils.printable.Predicates;
import org.junit.Test;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalsIgnoreCase;

public class Basic {
  @Test
  public void example1() {
    try {
      assertThat(
          "hello, world",
          asString().equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example2() {
    try {
      assertThat(
          "hello, world",
          asString("toUpperCase").equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example3() {
    try {
      assertThat(
          "hello, world",
          asString(call("toUpperCase").$()).equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example4() {
    try {
      assertThat(
          "hello, world",
          asString().check(call("toUpperCase").$(), equalsIgnoreCase("HELLO! WORLD")).$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example5() {
    try {
      assertThat(
          "hello, world",
          asString(call("toLowerCase").$())
              .check(call("toUpperCase").$(), equalsIgnoreCase("HELLO! WORLD"))
              .check(call("toUpperCase").$(), equalsIgnoreCase("HELLO! WORLD"))
              .$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

}
