package com.github.dakusui.thincrest.ut.examples;

import com.github.dakusui.thincrest.examples.ThincrestExample;
import com.github.dakusui.thincrest.metamor.MetamorExampleFailing;
import com.github.dakusui.thincrest.metamor.MetamorExamplePassing;
import com.github.dakusui.thincrest.utils.metatest.Metatest;
import com.github.dakusui.thincrest.utils.testbase.TestBase;
import org.junit.Test;

public class ThincrestExamplesTest extends TestBase {
  @Test
  public void testThincrestExample() {
    Metatest.verifyTestClass(ThincrestExample.class);
  }

  @Test
  public void testMetarmorExamplePassing() {
    Metatest.verifyTestClass(MetamorExamplePassing.class);
  }

  @Test
  public void testMetarmorExampleFailing() {
    Metatest.verifyTestClass(MetamorExampleFailing.class);
  }
}
