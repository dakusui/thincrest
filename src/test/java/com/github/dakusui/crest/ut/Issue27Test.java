package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.ExecutionFailure;
import com.github.dakusui.crest.core.Session;
import com.github.dakusui.crest.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.IOException;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static com.github.dakusui.crest.utils.printable.Predicates.matchesRegex;

public class Issue27Test extends TestBase {
  @Test
  public void addException() {
    Assert.assertFalse(Session.create().addException(new Throwable()).report().exceptions().isEmpty());
  }

  @Test(expected = IOException.class)
  public void stateful() throws IOException {
    try {
      Crest.assertThat(
          new StringBuilder(),
          asObject(
              call("append", "hello")
                  .andThen("append", "world")
                  .$())
              .check(
                  call("append", "!").andThen("append", "!").andThen("toString").$(),
                  equalTo("HELLOWORLD!")
              )
              .$()
      );
    } catch (RuntimeException e) {
      //    } catch (ComparisonFailure e) {
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("@append[hello]->@append[world](x)=<helloworld>")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void stateful2() throws IOException {
    try {
      Crest.assertThat(
          new StringBuilder(),
          asString(
              call("append", "hello")
                  .andThen("append", "world")
                  .andThen("toString")
                  .$())
              .equalTo("HelloWorld").$()
      );
    } catch (RuntimeException e) {
      //    } catch (ComparisonFailure e) {
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("@append[hello]->@append[world](x)=<helloworld>")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void simplest() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          asString("toLowerCase").check(
              call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
              equalTo('z')
          ).$()
      );
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("  y=@toLowerCase[](x)\n"
              + "    @toLowerCase[](x)=\"world\"\n"
              + "  @toUpperCase[](y)=\"WORLD\"\n"
              + "  @toUpperCase[]->@substring[2](y)=\"RLD\"\n"
              + "  @toUpperCase[]->@substring[2]->@charAt[1](y)=\"L\"")
      );
      throw new IOException();
    }
  }


  @Test(expected = IOException.class)
  public void simpler() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          allOf(
              asString("toLowerCase").check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('z')
              ).$())
      );
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("    y=@toLowerCase[](x)\n"
              + "      @toLowerCase[](x)=\"world\"\n"
              + "    @toUpperCase[](y)=\"WORLD\"\n"
              + "    @toUpperCase[]->@substring[2](y)=\"RLD\"\n"
              + "    @toUpperCase[]->@substring[2]->@charAt[1](y)=\"L\"")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void simple() throws IOException {
    try {
      Crest.assertThat(
          "WORLD",
          allOf(
              asString("toLowerCase").check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('z')
              ).$())
      );
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("    y=@toLowerCase[](x)\n"
              + "      @toLowerCase[](x)=\"world\"\n"
              + "    @toUpperCase[](y)=\"WORLD\"\n"
              + "    @toUpperCase[]->@substring[2](y)=\"RLD\"\n"
              + "    @toUpperCase[]->@substring[2]->@charAt[1](y)=\"L\"")
      );
      throw new IOException();
    }

  }

  @Test(expected = IOException.class)
  public void lessSimple() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", 1).$())
              .check(
                  call("toUpperCase").andThen("substring", 2).andThen("charAt", 1).$(),
                  equalTo('D'))
              .check(
                  call("replaceAll", "d", "DDD").andThen("concat", "XYZ").$(),
                  matchesRegex("xyz"))
              .$());
    } catch (ComparisonFailure e) {
      System.out.println("ACTUAL:" + e.getActual());
      System.out.println("EXPECTED: " + e.getExpected());
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("  y=@toLowerCase[]->@substring[1](x)\n"
              + "      @toLowerCase[](x)=\"world\"\n"
              + "      @toLowerCase[]->@substring[1](x)=\"orld\"\n"
              + "    @replaceAll[d, DDD](y)=\"orlDDD\"\n"
              + "    @replaceAll[d, DDD]->@concat[XYZ](y)=\"orlDDDXYZ")
      );
      throw new IOException();
    }
  }

  @Test(expected = IOException.class)
  public void givenErrorCausingCheck$whenAssertThat$thenMessageContainsIntendedString() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", 1).$())
              .check(
                  call("toUpperCase").andThen("substring", -2).andThen("charAt", 1).$(),
                  equalTo('z'))
              .$());
    } catch (ExecutionFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("  @toUpperCase[](y)=\"ORLD\"\n"
              + "  @toUpperCase[]->@substring[-2](y)=java.lang.StringIndexOutOfBoundsException(String index out of range: -2)\n"
              + "  @toUpperCase[]->@substring[-2]->@charAt[1](y)=java.lang.StringIndexOutOfBoundsException(String index out of range: -2)")
      );
      throw new IOException(e);
    }
  }


  @Test(expected = IOException.class)
  public void givenErrorCausingMatcher$whenAssertThat$thenMessageContainsIntendedString() throws IOException {
    try {
      Crest.assertThat("hello",
          "WORLD",
          asString(call("toLowerCase").andThen("substring", -1).$())
              .check(call("toUpperCase")
                      .andThen("substring", 1)
                      .andThen("charAt", 1).$(),
                  equalTo('z')
              ).$());
    } catch (ExecutionFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("  @toLowerCase[](x)=\"world\"\n"
              + "  @toLowerCase[]->@substring[-1](x)=java.lang.StringIndexOutOfBoundsException(String index out of range: -1)\n"
              + "FAILED")
      );
      throw new IOException(e);
    }
  }

  @Test(expected = IOException.class)
  public void partial() throws IOException {
    try {
      Crest.assertThat(
          "HELLO",
          Crest.not(
              asString().containsString("HELLO").$()
          )
      );
    } catch (ComparisonFailure e) {
      e.printStackTrace(System.out);
      Assert.assertThat(
          e.getMessage(),
          CoreMatchers.containsString("expected:<[not:[\n"
              + "  toString(x) containsString[HELLO]\n"
              + "]]> but was:<[when x=\"HELLO\"; then not:[\n"
              + "  toString(x) containsString[HELLO]\n"
              + "]->false]>")
      );
      throw new IOException(e);
    }
  }
}
