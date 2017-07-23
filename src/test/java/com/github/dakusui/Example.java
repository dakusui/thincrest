package com.github.dakusui;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.CrestFunctions;
import com.github.dakusui.crest.CrestPredicates;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.crest.CrestUtils.allOf;
import static org.junit.Assert.assertThat;

public class Example {
  @Test
  public void test() {
    List<String> aList = new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};

    assertThat(
        aList,
        allOf(
            Crest.<List<String>, Integer>builder(CrestFunctions.size()).and(CrestPredicates.equalsTo(2)).build(),
            Crest.<List<String>, String>builder(CrestFunctions.elementAt(0)).and(CrestPredicates.equalsTo("hello")).build()
        ));
  }
}
