package com.github.dakusui.crest;

import com.github.dakusui.crest.ut.CrestFunctionsTest;
import com.github.dakusui.crest.ut.CrestMatchersTest;
import com.github.dakusui.crest.ut.CrestPredicatesTest;
import com.github.dakusui.crest.ut.FormattableTest;
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
