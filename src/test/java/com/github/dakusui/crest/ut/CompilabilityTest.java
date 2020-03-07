package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.utils.ut.TestBase;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.crest.Crest.allOf;
import static com.github.dakusui.crest.Crest.assertThat;
import static com.github.dakusui.thincrest_pcond.functions.Functions.*;
import static com.github.dakusui.thincrest_pcond.functions.Predicates.alwaysTrue;
import static java.util.Arrays.asList;

public class CompilabilityTest {
  public static class Compilability extends TestBase {
    @Test
    public void whenAsObject$thenCompilableWithPredefinedFunctions() {
      List<String> aList = asList("A", "B", "C");

      assertThat(
          aList,
          allOf(
              Crest.<List<String>, String>asObject(stringify()).check(alwaysTrue()).matcher(),
              Crest.<List<String>, Object>asObject(elementAt(0)).check(alwaysTrue()).matcher(),
              Crest.<List<String>, Object>asObject(size()).check(alwaysTrue()).matcher(),
              Crest.<List<String>, Object>asObject(stream()).check(alwaysTrue()).matcher(),
              Crest.<List<String>, Object>asObject("toString").check(alwaysTrue()).matcher()
          )
      );
    }
  }
}
