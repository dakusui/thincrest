package com.github.dakusui.crest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public enum CrestUtils {
  ;

  static final        PrintStream STDOUT = System.out;
  static final        PrintStream STDERR = System.err;
  public static final PrintStream NOP    = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) throws IOException {
    }
  });

  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  public static void suppressStdOutErrIfRunUnderSurefire() {
    if (CrestUtils.isRunUnderSurefire()) {
      System.setOut(NOP);
      System.setErr(NOP);
    }
  }

  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  public static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setOut(STDERR);
  }

  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }

}
