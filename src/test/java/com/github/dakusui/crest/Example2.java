package com.github.dakusui.crest;

import com.github.dakusui.crest.CrestPredicates;
import org.junit.Test;

import java.util.function.Predicate;

public class Example2 {
  @Test
  public void test() {
    Predicate<Integer> predicate = CrestPredicates.eq(100).and(CrestPredicates.eq(200));
    System.out.println(predicate);
    System.out.println(predicate.test(123));
  }
}
