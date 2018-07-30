package com.github.dakusui.crest.utils;

import com.github.dakusui.crest.utils.ut.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    FaultSourceTest.class,
    FunctionsTest.class,
    ParameterizedFunctionsTest.class,
    ParameterizedPredicatesTest.class,
    PredicatesTest.class,
})
public class UtilsAll {
}
