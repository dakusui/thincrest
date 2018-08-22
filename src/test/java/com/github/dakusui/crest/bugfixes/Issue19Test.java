package com.github.dakusui.crest.bugfixes;

import com.github.dakusui.crest.Crest;
import org.junit.Test;

import static com.github.dakusui.crest.Crest.asBoolean;
import static com.github.dakusui.crest.Crest.call;

public class Issue19Test {
  static class Sut {
    /**
     * This method is called reflectively during unit test.
     *
     * @return none
     */
    @SuppressWarnings("unused")
    public String runtimeException() {
      throw new RuntimeException("runtimeexception");
    }

    /**
     * This method is called reflectively during unit test.
     *
     * @return none
     */
    public String error() {
      throw new Error("error");
    }

    /**
     * This method is called reflectively during unit test.
     *
     * @return this
     */
    public Sut normal() {
      return this;
    }
  }

  @Test
  public void test() {
    Crest.assertThat(
        "hello",
        asBoolean(call("toString").$()).isTrue().$()
    );
  }

  @Test
  public void givenRuntimeExceptionDuringMatch$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("runtimeException").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenRuntimeExceptionDuringMatchAtSecondCall$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("normal").andThen("runtimeException").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenErrorDuringMatch$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("error").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenErrorDuringMatchAtSecondCall$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("normal").andThen("error").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenNoSuchMethodAtFirstCall$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("noSuchMethod").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenNoSuchMethodAtSecondCall$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.asString(call("normal").andThen("noSuchMethod").$()).containsString("hello").$()
    );
  }

  @Test
  public void givenNoSuchMethodAtFirstCallInsideAllOf$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.allOf(
            Crest.asString(call("noSuchMethod").$()).containsString("hello").$()
        )
    );
  }

  @Test
  public void givenNormal$when$then() {
    Crest.assertThat(
        new Sut(),
        Crest.allOf(
            Crest.asString(call("normal").andThen("toString").$()).containsString("hello").$()
        )
    );
  }

}
