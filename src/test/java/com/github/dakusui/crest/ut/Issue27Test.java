package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.ExecutionFailure;
import com.github.dakusui.crest.core.Report;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static com.github.dakusui.crest.utils.printable.Predicates.matchesRegex;

@Ignore //WIP
public class Issue27Test {
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


  @Test
  public void simple() {
    try {
      Report.assertThat("hello",
          "WORLD",
          allOf(
              asString("toLowerCase")
                  .check(
                      call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                      equalTo('z'))
                  .check(
                      call("replaceAll", "d", "DDD").andThen("concat", "XYZ").$(),
                      matchesRegex("xyz"))
                  .$()));
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      throw e;
    }
  }

  @Test
  public void lessSimple() {
    try {
      Report.assertThat("hello",
          "WORLD",
          allOf(
              asString(call("toLowerCase").andThen("substring", 1).$())
                  .check(
                      call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                      equalTo('D'))
                  .check(
                      call("replaceAll", "d", "DDD").andThen("concat", "XYZ").$(),
                      matchesRegex("xyz"))
                  .$()));
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      throw e;
    }
  }

  @Test
  public void error() {
    try {
      Report.assertThat("hello",
          "WORLD",
          allOf(
              asString(call("toLowerCase").andThen("substring", 1).$())
                  .check(
                      call("toUpperCase").andThen("substring", -2).andThen("charAt", 1).$(),
                      equalTo('z'))
                  .$()));
    } catch (ExecutionFailure e) {
      e.printStackTrace(System.out);
      throw e;
    }
  }


  @Test
  public void error2() {
    try {
      Report.assertThat("hello",
          "WORLD",
          allOf(
              asString(call("toLowerCase").andThen("substring", 1).$())
                  .check(
                      call("toUpperCase").andThen("substring", -2).andThen("charAt", 1).$(),
                      equalTo('z'))
                  .$()));
    } catch (ExecutionFailure e) {
      e.printStackTrace(System.out);
      throw e;
    }
  }

  @Test
  public void partial() {
    Crest.assertThat(
        "HELLO",
        Crest.not(
            asString().containsString("HELLO").$()
        )
    );
  }

}
