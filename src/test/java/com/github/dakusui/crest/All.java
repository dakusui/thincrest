package com.github.dakusui.crest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CrestFunctionsTest.class,
    CrestMatchersTest.class,
    CrestPredicatesTest.class,
    FormattableTest.class
})
public class All {
}
