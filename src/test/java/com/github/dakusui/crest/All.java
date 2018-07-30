package com.github.dakusui.crest;

import com.github.dakusui.crest.examples.ExamplesTest;
import com.github.dakusui.crest.ut.*;
import com.github.dakusui.crest.utils.UtilsAll;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CrestFunctionsTest.class,
    CrestTest.class,
    CrestPredicatesTest.class,
    PrintableTest.class,
    InternalUtilsTest.class,
    MatcherTest.class,
    ExamplesTest.class,
    UtilsAll.class
})
public class All {
}
