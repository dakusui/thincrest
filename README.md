# thincrest
**thincrest**(pronounced 'think rest') is a thin wrapper library for **Hamcrest**[[1]].

# Background 
Following is an excerpt from a wikipedia article about the history of assertion 
mechanisms until the time when **Hamcrest** came to exist. 


>"First generation" unit test frameworks provide an 'assert' statement, allowing 
one to assert during a test that a particular condition must be true. If the condition 
is false, the test fails. For example:
>
>```
>assert(x == y);
>```
>
>But this syntax fails to produce a sufficiently good error message if 'x' and 'y' 
are not equal. It would be better if the error message displayed the value of 'x' 
and 'y'. To solve this problem, "second generation" unit test frameworks provide a 
family of assertion statements, which produce better error messages. For example,
>
>```
>assert_equal(x, y);
>assert_not_equal(x, y);
>```
>
>But this leads to an explosion in the number of assertion macros, as the above 
set is expanded to support comparisons different from simple equality. So 
"third generation" unit test frameworks use a library such as Hamcrest to support 
an 'assert_that' operator that can be combined with 'matcher' objects, leading to 
syntax like this:
>
>```
>assert_that(x, equal_to(y))
>assert_that(x, is_not(equal_to(y)))
>```
>
>The benefit is that you still get fluent error messages when the assertion fails, 
but now you have greater extensibility. It is now possible to define operations 
that take matchers as arguments and return them as results, leading to a grammar 
that can generate a huge number of possible matcher expressions from a small number 
of primitive matchers.
>
>These higher-order matcher operations include logical connectives (and, or and not),
 and operations for iterating over collections. This results in a rich matcher 
 language which allows complex assertions over collections to be written in a 
 declarative style rather than a procedural style.
>
> -- <cite>Wikipedia article on "Hamcrest"</cite>[[3]]

After Hamcrest, people came to want IDE to help them because they found it is
tedious to find an appropriate matcher class or its factory method for their needs.
Solutions to it were, **Assert J**[[4]], **Google Truth**[[5]], etc.

The author of **thincrest** still feels the testing process can become sometimes
painful when one fails.

When you verify a feature under test, there are input for the feature and output
from it. And output is what we are going to verify. But output is not only simple
types such as ```String```, ```int```, ```boolean```. In most non-trivial cases,
it has its own structure. 



What a matcher or an assertion does can be divided three parts, which are transforming
(or extracting value from) SUT's output, verify if the value satisfies a certain
 characteristic or maybe characteristics, and if it fails, compose a message that 
 describes the failure.
 
In Java8, the transformation can be done by a function while the verification can
 be achieved by a predicate.

**thincrest** is a library to support building custom matchers for **Hamcrest** by
combinating functions and predicates instead of writing large number of order made
matchers one by one.

## Features
* Comprehensive ouput on help, that lets you understand what is going on
  overall. Eliminates run->fail->fix->next run->next fail->next fix->next next run->...
  loop
* Building a matcher instance from a function and predicates.
* Fluency on building matcher instances

# Usage
## Installation
**thincrest** requires Java SE8 or later. Following is a maven coordinate for it.


```xml

    <dependency>
      <groupId>com.github.dakusui</groupId>
      <artifactId>thincrest</artifactId>
      <version>[2.0.0,)</version>
      <scope>test</scope>
    </dependency>
```

Once you install the dependency, lets static import **thincrest**'s facade ```Crest```
to use it from your test class.

```java
import static com.github.dakusui.crest.matcherbuilders.Crest.*;
```

This enables you to use following static methods without typing the class name.

* allOf(Matcher<? super T>... matchers)
* anyOf(Matcher<? super T>... matchers)
* asObject(...)
* asComparableOf(...)
* asString(...)
* asStream(...)
* asList(...)
* assertThat(...)

## Examples

Following is a first simple test written by **thincrest**.

```java

    public class Examples {
      @Test
      public void helloWorldThincrest() {
        assertThat(
            Arrays.asList("Hello", "world"),
            asString().matchesRegex("HELLO").any()
        );
      }
    }
```

The static method ```asString``` requests to convert a given value to a ```java.lang.String```
by calling ```toString``` method on it. The method of it, ```matchesReges``` checks
if the converted value matches a given regular expression, in this example it's
```HELLO```, and in case it doesn't pass the verification, the test will fail.

**thincrest** also has its own ```allOf``` and ```anyOf``` matcher as hamcrest does.
But the implementation of them by **thincrest** is a bit different from their original
versions.



```java

    public class Examples {
      @Test
      public void helloAllOfTheWorldThincrest() {
        assertThat(
            Arrays.asList("Hello", "world"),
            allOf(
                asStream().allMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher(),
                asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
                asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher()
            )
        );
      }
    }

```

```
  @Test
  public void givenList_Hello_world_$whenAsStreamAndFailingConditionComposedBy_allOf_method$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        allOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndPassingConditionComposedBy_anyOf_method$thenPass() {
    assertThat(
        Arrays.asList("Hello", "world"),
        anyOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void givenList_Hello_world_$whenAsStreamAndFailingConditionComposedBy_anyOf_method$thenFail() {
    assertThat(
        Arrays.asList("Hello", "world"),
        anyOf(
            asStream().allMatch(Formattable.predicate("==bye", "bye"::equals)).matcher(),
            asStream().noneMatch(Formattable.predicate("==bye", "bye"::equals).negate()).matcher(),
            asStream().anyMatch(Formattable.predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void test8_b$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject("toString").equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject().equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8a$thenFail() {
    assertThat(
        "aStringToBeExamined",
        asObject().equalTo("aStringToBeExamined2").all()
    );

    assertThat(
        "aStringToBeExamined",
        asObject().all()
    );

    assertThat(
        "aStringToBeExamined",
        asString().equalTo("aStringToBeExamined2").all()
    );
  }


  @Test
  public void given_DeBelloGallicco_$whenAllOfAsStringFailingAndAsObjectFailingMatchers$thenFail() {
    assertThat(
        "Gallia est omnis divisa in partes tres, quarun unum incolunt Belgae, "
            + "alium Aquitani, tertium linua ipsorum Celtae, nostra Galli appelantur",
        allOf(
            asString().check("contains", "est").containsString("Caesar").matcher(),
            asObject("length").check(Formattable.predicate(">1024", o -> ((Integer) o) > 1024)).matcher()
        )
    );
  }

  @Test
  public void givenEmptyString$whenAsObjectByMethodCallAndDoFailingCheckByMethodCall$thenFail() {
    assertThat(
        "",
        asObject("length").check("equals", "hello").matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndFailingCheck$thenFail() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").gt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").lt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByPresetLengthFunctionAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparable(CrestFunctions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_Hello_world_$whenAsComparableAndDoPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        asComparable(CrestFunctions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_50_$whenAsComparableAndDoFailingCheck$thenFail() {
    assertThat(
        50,
        Crest.asComparableOf(Integer.class).ge(5).lt(50).all()
    );
  }


  @Test
  public void givenList$$whenContains101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().contains(101).matcher()
    );
  }

  @Test
  public void givenList$$whenContains100$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().contains(100).matcher()
    );
  }


  @Test
  public void givenList$$whenContainsAll101$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().containsAll(Arrays.asList(100, 101)).matcher()
    );
  }

  @Test
  public void givenList$$whenContainsAll100and200$thenPass() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().containsAll(Arrays.asList(100, 200)).matcher()
    );
  }

  @Test
  public void givenList$$whenIsEmpty$thenFail() {
    assertThat(
        Arrays.asList(100, 200, 300, 400, 500),
        Crest.<Integer>asList().isEmpty().matcher()
    );
  }

  @Test
  public void givenEmptyList$$whenIsEmpty$thenPass() {
    assertThat(
        Collections.emptyList(),
        Crest.asList().isEmpty().matcher()
    );
  }

}
```

# References
* [0] "JUnit"
* [1] "Hamcrest"
* [2] "Summary of Changes in version 4.4", JUnit team
* [3] "Hamcrest" article in Wikipedia
* [4] "Assert J"
* [5] "Google Truth"

[0]: http://junit.org/junit4/
[1]: http://hamcrest.org/
[2]: https://github.com/junit-team/junit4/blob/master/doc/ReleaseNotes4.4.md#summary-of-changes-in-version-44
[3]: https://en.wikipedia.org/wiki/Hamcrest
[4]: http://google.github.io/truth/
[5]: http://google.github.io/truth/
