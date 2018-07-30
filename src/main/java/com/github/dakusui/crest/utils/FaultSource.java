package com.github.dakusui.crest.utils;


import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.String.format;

public interface FaultSource {
  default <T> T requireValue(Predicate<T> condition, T value) {
    return require(condition, value, this::exceptionForIllegalValue, c -> v -> messageComposer(c, v));
  }

  default <T> T requireState(Predicate<T> condition, T value) {
    return require(condition, value, this::exceptionForIllegalState, c -> v -> messageComposer(c, v));
  }

  default <T> T requireNext(Predicate<T> condition, T value) {
    return require(condition, value, this::exceptionForNoSuchElement, c -> v -> messageComposer(c, v));
  }

  default <T> T requireNonNull(T value) {
    return requireNonNull(value, () -> null);
  }

  default <T> T requireNonNull(T value, String message) {
    return requireNonNull(value, () -> message);
  }

  default <T> T requireNonNull(T value, Supplier<String> messageSupplier) {
    if (value != null)
      return value;
    throw exceptionForNullValue(messageSupplier.get());
  }

  default <T> T require(Predicate<T> condition, T value, Function<String, RuntimeException> exceptionFactory, Function<Predicate<T>, Function<T, String>> messageComposer) {
    if (condition.test(value))
      return value;
    throw exceptionFactory.apply(messageComposer.apply(condition).apply(value));
  }

  default <T> String messageComposer(Predicate<T> c, T v) {
    return format("'%s' did not satisfy '%s'", v, c);
  }

  default RuntimeException impossibleLineReached() {
    throw impossibleLineReached("This line is not expected to be executed");
  }

  default RuntimeException impossibleLineReached(String message) {
    throw exceptionForImpossibleLine(message);
  }

  default RuntimeException failureCaught(Throwable t) {
    throw failureCaught(t, s -> t.getMessage());
  }

  default RuntimeException failureCaught(Throwable t, Function<Throwable, String> messageComposer) {
    if (t instanceof Error)
      throw (Error) t;
    throw exceptionForCaughtFailure(messageComposer.apply(t), t);
  }

  default void requireNotInterrupted() throws InterruptedException {
    if (Thread.interrupted())
      throw exceptionForCaughtInterruptedException();
  }


  default RuntimeException exceptionForIllegalState(String s) {
    return new IllegalStateException(s);
  }

  default RuntimeException exceptionForNoSuchElement(String s) {
    return new NoSuchElementException(s);
  }

  default RuntimeException exceptionForNullValue(String message) {
    throw new NullPointerException(message);
  }

  default RuntimeException exceptionForImpossibleLine(String message) {
    throw new AssertionError(message);
  }

  default InterruptedException exceptionForCaughtInterruptedException() {
    return new InterruptedException();
  }

  RuntimeException exceptionForCaughtFailure(String message, Throwable t);

  RuntimeException exceptionForIllegalValue(String message);
}