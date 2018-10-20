package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.Eater;
import com.github.dakusui.crest.utils.printable.Predicates;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.allOf;
import static com.github.dakusui.crest.Crest.asString;
import static com.github.dakusui.crest.Crest.call;
import static com.github.dakusui.crest.core.Eater.listEater;
import static com.github.dakusui.crest.core.Eater.regexEater;
import static com.github.dakusui.crest.core.Eater.simpleListEater;
import static com.github.dakusui.crest.utils.printable.Predicates.equalTo;
import static com.github.dakusui.crest.utils.printable.Predicates.matchesRegex;
import static java.util.Arrays.asList;

public class EaterTest {
  @Test
  public void main() {
    List<String> target = asList("Z", "abc", "Z", "def", "Z", "xyz", "Z");
    List<String> elements = asList(
        "abc",
        "def",
        "XYZ"
    );
    System.out.println(elements);
    for (String s : elements) {
      Eater<List<String>, String> eater = simpleListEater(target);
      Optional<List<String>> rest = eater.find(s);
      if (rest.isPresent())
        target = rest.get();
      else
        throw new RuntimeException(String.format("Not found:'%s' not found in %s", s, target));
      System.out.println("Met: " + s + ": rest=" + rest.get());
    }
  }

  @Test
  public void main2() {
    String target = "ZabcZdefZxyzZ";
    String[] regeges = { "ab.", "d.f", "XYZ" };
    for (String regex : regeges) {
      Eater<String, String> eater = regexEater(target);
      Optional<String> rest = eater.find(regex);
      if (rest.isPresent())
        target = rest.get();
      else
        throw new RuntimeException("Not found:'" + regex + "' not found in '" + target + "'");
      System.out.println("Met: " + regex + ": rest=" + rest.get());
    }
  }

  @Test
  public void main3() {
    List<String> target = asList("Z", "abc", "Z", "def", "Z", "xyz", "Z");
    List<Predicate<String>> predicates = asList(
        matchesRegex("ab."),
        matchesRegex("d.f"),
        matchesRegex("XYZ")
    );
    System.out.println(predicates);
    for (Predicate<String> p : predicates) {
      Eater<List<String>, Predicate<String>> eater = listEater(target);
      Optional<List<String>> rest = eater.find(p);
      if (rest.isPresent())
        target = rest.get();
      else
        throw new RuntimeException(String.format("Not found:'%s' not found in %s", p, target));
      System.out.println("Met: " + p + ": rest=" + rest.get());
    }
  }

  @Test
  public void test() {
    Crest.assertThat(
        "hello",
        allOf(
            asString(
                call("toString").andThen("toString").$())
                .check(
                    equalTo("hello").and(matchesRegex("H"))
                ).$()));
  }
}
