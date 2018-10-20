package com.github.dakusui.crest.core;

import com.github.dakusui.crest.utils.printable.Predicates;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public interface Eater<T /* Target container */, U /* Component to be searched for*/> {
  Optional<T> find(U component);

  static Eater<String, String> stringEater(String target) {
    return component -> {
      int index = target.indexOf(requireNonNull(component));
      return index < 0
          ? Optional.empty()
          : Optional.of(target.substring(index + component.length()));
    };
  }

  static Eater<String, String> regexEater(String target) {
    return regexString -> {
      Matcher matcher = Pattern.compile(String.format("(%s)", regexString)).matcher(target);
      return matcher.find()
          ? stringEater(target).find(matcher.group(1))
          : Optional.empty();
    };
  }

  static <E> Eater<List<E>, E> simpleListEater(List<E> target) {
    return e -> listEater(target).find(Predicates.equalTo(e));
  }

  static <E> Eater<List<E>, Predicate<E>> listEater(List<E> target) {
    return p -> {
      int index = -1;
      int i = 0;
      for (E element : target) {
        if (p.test(element)) {
          index = i;
          break;
        }
        i++;
      }
      return index < 0
          ? Optional.empty()
          : Optional.of(target.subList(index + 1, target.size()));
    };
  }
}
