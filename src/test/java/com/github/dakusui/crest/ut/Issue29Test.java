package com.github.dakusui.crest.ut;

import org.junit.Test;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.crest.utils.printable.Functions.THIS;

public class Issue29Test {
  @Test
  public void useStrict() {
    assertThat(
        "HELLO",
        allOf(
            asChar("charAt", arg(int.class, 1)).equalTo('E').$(),
            asString("substring", arg(int.class, 1), arg(Integer.class, 3)).equalTo("EL").$()
        )
    );
  }

  @SuppressWarnings("UnnecessaryBoxing")
  @Test
  public void useStrict2() {
    assertThat(
        "HELLO",
        asString("substring", arg(int.class, 1), arg(Number.class, Integer.valueOf(3))).equalTo("EL").$()
    );
  }

  @Test
  public void useStrictForStaticMethodWithVarArgs() {
    assertThat(
        "HELLO",
        asString(
            call(String.class, "format", "%s WORLD %s", args(String.class, "hello", "!")).$())
            .equalTo("hello WORLD !")
            .$());
  }

  @Test
  public void useTHISkeyword() {
    assertThat(
        "HELLO",
        asBoolean(
            call("equals",
                arg(Object.class, THIS)
            ).$())
            .equalTo(true)
            .$());
  }

  @Test
  public void useStrictForStaticMethodWithVarArgsWithTHISobject() {
    assertThat(
        "HELLO",
        asString(
            call(String.class, "format", "%s %s WORLD %s",
                args(Object.class, "hello", THIS, "!")).$())
            .equalTo("hello HELLO WORLD !")
            .$());
  }

  @Test
  public void useStrictForOverMethod() {
    try {
      assertThat(
          new OverloadedMethods(),
          allOf(
              asString(call("overloaded", arg(String.class, "value")).$())
                  .equalTo("str:value")
                  .$(),
              asString(call("overloaded", arg(Integer.class, 123)).$())
                  .equalTo("str:value")
                  .$(),
              asString(call("overloaded", arg(int.class, 123)).$())
                  .equalTo("str:value")
                  .$(),
              asString(call("overloaded", arg(Object.class, "value")).$())
                  .equalTo("str:value")
                  .$()
          ));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  public static class OverloadedMethods {
    public String overloaded(String value) {
      return String.format("str:%s", value);
    }

    public String overloaded(Integer value) {
      return String.format("Integer:%s", value);
    }

    public String overloaded(int value) {
      return String.format("int:%s", value);
    }

    public String overloaded(Object value) {
      return String.format("obj:%s", value);
    }

  }
}
