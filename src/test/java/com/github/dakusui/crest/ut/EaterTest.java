package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.ExecutionFailure;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static com.github.dakusui.crest.Crest.*;
import static java.util.Arrays.asList;

public class EaterTest {
  @Test(expected = NoSuchElementException.class)
  public void test2() throws Throwable {
    String targetContainer = "ZabcZdefZxyzZ";
    try {
      assertThat(
          targetContainer,
          allOf(
              asString(afterRegex("ab.").after("d.f").after("XYZ").$()).equalTo("Z").$()
          )
      );
    } catch (ExecutionFailure e) {
      e.printStackTrace();
      throw e.getCause();
    }
  }

  @Test
  public void test3() {
    String targetContainer = "ZabcZdefZxyzZ";
    assertThat(
        targetContainer,
        allOf(
            asString(afterRegex("ab.").after("d.f").after("xyz").$()).equalTo("Z").$()
        )
    );
  }

  @Test(expected = NoSuchElementException.class)
  public void test4() throws Throwable {
    List<String> targetContainer = asList("Z", "abc", "Z", "def", "Z", "xyz", "Z");
    try {
      assertThat(
          targetContainer,
          Crest.allOf(
              asListOf(String.class,
                  afterElement("abc").after("def").after("XYZ").$()).isNotNull().$()
          )
      );
    } catch (ExecutionFailure e) {
      throw e.getCause();
    }
  }


}
