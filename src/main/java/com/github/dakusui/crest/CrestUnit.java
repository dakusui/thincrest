package com.github.dakusui.crest;

import org.junit.After;
import org.junit.Before;

public abstract class CrestUnit {
  @Before
  public void before() {
    CrestUtils.suppressStdOutErrIfRunUnderSurefire();
  }

  @After
  public void after() {
    CrestUtils.restoreStdOutErr();
  }
}
