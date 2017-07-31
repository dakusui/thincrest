package com.github.dakusui.crest.examples;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class Sandbox {
  public static void main(String[] args) {
    // Avoid getting error from IntelliJ
    String[] value = require(args, (Predicate<? super String[]>) isNotNull());
    System.out.println(Arrays.toString(value));
  }

  private static <T> T require(T value, Predicate<? super T> predicate) {
    if (predicate.test(value))
      return value;
    throw new RuntimeException();
  }

  private static <T> Predicate<? super T> isNotNull() {
    return (Predicate<T>) Objects::nonNull;
  }
}
