package com.github.dakusui.crest.examples;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class InAssertJ {

  @Test
  public void test() {
    List<String> list = Arrays.asList("hoge", "fuga", "piyo");

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(true)
        .isTrue();
    softly.assertThat("hoge")
        .isEqualTo("hoge");
    softly.assertThat(list)
        .isNotEmpty()
        .contains("hoge", "fuga", "piyo", "poyo");

    softly.assertAll();
  }
}
