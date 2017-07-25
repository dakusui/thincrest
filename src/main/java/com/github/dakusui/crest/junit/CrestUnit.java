package com.github.dakusui.crest.junit;

import com.github.dakusui.crest.core.CrestUtils;
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
