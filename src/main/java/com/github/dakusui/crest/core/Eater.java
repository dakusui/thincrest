package com.github.dakusui.crest.core;

import com.github.dakusui.crest.utils.printable.Printable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public interface Eater<T /* Target*/, C /* Target container */> {
  Eater after(T target);

  /**
   * Builds a function.
   *
   * @return The function.
   */
  Function<C, C> build();

  /**
   * A synonym of {@code build} method.
   *
   * @return A built function.
   */
  default Function<C, C> $() {
    return build();
  }

  abstract class Base<T, C> implements Eater<T, C> {
    final T           target;
    final Eater<T, C> parent;

    Base(Eater<T, C> parent, T target) {
      this.parent = parent;
      this.target = requireNonNull(target);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Function<C, C> build() {
      return this.parent == null
          ? toFunction()
          : this.parent.build().andThen(toFunction());
    }

    Function<C, C> toFunction() {
      return ChainedFunction.create(
          Printable.function(
              describeFunction(),
              createFunction()
          ));
    }

    String describeFunction() {
      return String.format("->after[%s]", this.target);
    }

    protected abstract Function<C, C> createFunction();

  }

  class RegexEater extends Base<String, String> {
    public RegexEater(Eater<String, String> parent, String target) {
      super(parent, target);
    }

    /**
     * @param target A regex
     * @return A new {@code RegexEater}
     */
    @Override
    public RegexEater after(String target) {
      return new RegexEater(this, target);
    }

    protected Function<String, String> createFunction() {
      return (String container) -> {
        Matcher matcher = Pattern.compile(String.format("(%s)", target)).matcher(container);
        if (matcher.find())
          return restOf(container, matcher.group(1));
        throw new NoSuchElementException(String.format("regex:%s was not found", target));
      };
    }

    private String restOf(String container, String matched) {
      return container.substring(matched.length() + container.indexOf(matched));
    }
  }

  class ListEater<T> extends Base<T, List<T>> {

    public ListEater(Eater<T, List<T>> parent, T target) {
      super(parent, target);
    }

    @Override
    public Eater after(T target) {
      return new ListEater<>(this, target);
    }

    @Override
    protected Function<List<T>, List<T>> createFunction() {
      return container -> {
        int index = container.indexOf(ListEater.this.target);
        if (index < 0)
          throw new NoSuchElementException(String.format("Element:%s was not found", target));
        return container.subList(index + 1, container.size());
      };
    }
  }
}
