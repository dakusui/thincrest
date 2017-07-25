package com.github.dakusui.crest;

import com.github.dakusui.crest.ut.CrestFunctionsTest;
import com.github.dakusui.crest.ut.MatcherBuildersTest;
import com.github.dakusui.crest.ut.CrestPredicatesTest;
import com.github.dakusui.crest.ut.FormattableTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CrestFunctionsTest.class,
    MatcherBuildersTest.class,
    CrestPredicatesTest.class,
    FormattableTest.class
})
public class All {
}
