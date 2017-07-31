package com.github.dakusui.crest.core;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Causes of exceptions
 * * checks
 * * input
 * * arguments
 * * user input
 * * state
 * * configuration
 * * environment
 * * output from externals
 * other system
 * other libraries
 * * failure
 * exception returned by underlying libraries
 */
public interface Variable {
  enum Category implements Variable {
    ARGUMENT {
      @SuppressWarnings("unchecked")
      @Override
      public <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value) {
        return () -> (E) new IllegalArgumentException(messageSupplier.get());
      }
    },
    STATE {
      @SuppressWarnings("unchecked")
      @Override
      public <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value) {
        return () -> (E) new IllegalStateException(messageSupplier.get());
      }
    },
    CONFIGURATION {
      @SuppressWarnings("unchecked")
      @Override
      public <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value) {
        return () -> (E) new IllegalStateException(messageSupplier.get());
      }
    },
    ENVIRONMENT {
      @SuppressWarnings("unchecked")
      @Override
      public <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value) {
        return () -> (E) new IllegalStateException(messageSupplier.get());
      }
    },
    EXCEPTION {
      @SuppressWarnings("unchecked")
      @Override
      public <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value) {
        return () -> {
          if (value instanceof Throwable)
            if (value instanceof RuntimeException || value instanceof Error)
              return (E) value;
            else
              return (E) new RuntimeException(messageSupplier.get(), (Throwable) value);
          throw new RuntimeException(String.format("Non throwable value '%s' was given.", value));
        };
      }
    }
  }

  default <E extends Throwable, T> T require(T value, Predicate<? super T> condition, Supplier<String> messageSupplier) throws E {
    return require(null, value, condition, messageSupplier);

  }

  default <E extends Throwable, T> T require(T value, Predicate<? super T> condition) throws E {
    return require(null, value, condition);
  }

  default <E extends Throwable, T> T require(String variableName, T value, Predicate<? super T> condition) throws E {
    return require(variableName, value, condition, null);
  }

  <T, E extends Throwable> Supplier<? extends E> exceptionFactory(Supplier<String> messageSupplier, T value);

  default <E extends Throwable, T> T require(String variableName, T value, Predicate<? super T> condition, Supplier<String> messageSupplier) throws E {
    Objects.requireNonNull(this);
    Objects.requireNonNull(condition);
    if (!condition.test(value))
      throw this.<T, E>exceptionFactory(
          messageSupplier != null ?
              messageSupplier :
              () -> String.format(
                  "%s requirement: %s(=%s) %s was not satisfied",
                  this,
                  variableName == null ?
                      "noname" :
                      variableName,
                  value,
                  condition
              ),
          value
      ).get();
    return value;
  }
}
