package com.github.dakusui.crest.core;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Precondition {
  ARGUMENT {
    @SuppressWarnings("unchecked")
    @Override
    <E extends Throwable> Supplier<? extends E> createExceptionSupplier(Supplier<String> messageSupplier) throws E {
      return () -> (E) new IllegalStateException(messageSupplier.get());
    }
  },
  STATE {
    @SuppressWarnings("unchecked")
    @Override
    <E extends Throwable> Supplier<? extends E> createExceptionSupplier(Supplier<String> messageSupplier) throws E {
      return () -> (E) new IllegalStateException(messageSupplier.get());
    }
  };

  <E extends Throwable, T> T require(T value, Predicate<? super T> condition, Supplier<String> messageOnFailure) throws E {
    return require(this, condition, value, messageOnFailure);
  }


  <E extends Throwable, T> T require(T value, Predicate<? super T> condition) throws E {
    return this.require(value, condition, () -> String.format("%s requirement '%s' was not satisfied by value '%s'", this, condition, value));
  }

  abstract <E extends Throwable> Supplier<? extends E> createExceptionSupplier(Supplier<String> messageSupplier) throws E;

  <E extends Throwable> E throwException(Supplier<String> messageSupplier) throws E {
    throw createExceptionSupplier(messageSupplier).get();
  }

  private static <E extends Throwable, T> T require(Precondition precondition, Predicate<? super T> condition, T value, Supplier<String> messageOnFailure) throws E {
    if (!condition.test(value))
      throw precondition.<E>throwException(messageOnFailure);
    return value;
  }

  public static <T> T requireState(Predicate<T> condition, T value, String messageOnFailure) {
    return STATE.require(value, condition, () -> messageOnFailure);
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T requireState(Predicate<T> check, T value) {
    return requireState(check, value, (String) null);
  }

  public static void requireState(boolean stateCondition) {
    requireState(v -> v, stateCondition);
  }

  public static <T> T require(Precondition argument, T value, Predicate<? super T> condition) {
    return argument.require(value, condition);
  }
}
