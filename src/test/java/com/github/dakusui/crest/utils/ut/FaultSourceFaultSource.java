package com.github.dakusui.crest.utils.ut;

import com.github.dakusui.crest.utils.FaultSource;

public enum FaultSourceFaultSource implements FaultSource {
  PRODUCTION {
    @Override
    public RuntimeException exceptionForCaughtFailure(String message, Throwable t) {
      throw new RuntimeException(message, t);
    }

    @Override
    public RuntimeException exceptionForIllegalValue(String message) {
      throw new IllegalArgumentException(message);
    }
  }
}
