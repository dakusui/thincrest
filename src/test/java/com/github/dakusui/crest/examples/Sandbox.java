package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.core.Printable;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.allOf;
import static com.github.dakusui.crest.Crest.asListOf;
import static com.github.dakusui.crest.Crest.assertThat;
import static java.util.Arrays.asList;

public class Sandbox {
  public static void main(String[] args) {
    new Sandbox().helloAllOfTheWorldThincrest();
  }

  private static <T> T require(T value, Predicate<? super T> predicate) {
    if (predicate.test(value))
      return value;
    throw new RuntimeException();
  }

  private static <T> Predicate<? super T> isNotNull() {
    return (Predicate<T>) Objects::nonNull;
  }


  @Test
  public void helloAllOfTheWorldThincrest() {
    assertThat(
        asList("Hello", "world"),
        allOf(
            asListOf(String.class).allMatch(Printable.predicate("==bye", "bye"::equals)).matcher(),
            asListOf(String.class).noneMatch(Printable.predicate("==bye", "bye"::equals)).matcher(),
            asListOf(String.class).anyMatch(Printable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }
}
