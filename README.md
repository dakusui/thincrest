# thincrest

**thincrest**(pronounced 'think rest') is a small library that does assertions
 such as **Hamcrest**[[1]] and **Assert J**[[2]]. It is designed to be able to
  balance following values
  
 * Easy to implement user custom matchers.
 * Avoid fail->fix->run->fail->fix->run loop.
 * Readable messages on failures.
 * Readable test codes.
 * IDE friendliness.
 
It used to be a thin wrapper for **Hamcrest** ant it is the reason why this library
was initially named so, but it is not anymore.

# Usage
## Installation
**thincrest** requires Java SE8 or later. Following is a maven coordinate for it.

```xml

    <dependency>
      <groupId>com.github.dakusui</groupId>
      <artifactId>thincrest</artifactId>
      <version>[3.2.0,)</version>
      <scope>test</scope>
    </dependency>
```

Once you install the dependency, lets static import **thincrest**'s facade ```Crest```
to use it from your test class.

```java
import static com.github.dakusui.crest.Crest.*;
```

This enables you to use following static methods without typing the class name.

* allOf(Matcher<? super T>... matchers)
* anyOf(Matcher<? super T>... matchers)
* asObject(...)
* asComparableOf(...)
* asString(...)
* asObjectList(...)
* asListOf(...)
* asObjectMap(...)
* asMapOf(...)
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
                asListOf(String.class).allMatch(Printable.predicate("==bye", "bye"::equals)).matcher(),
                asListOf(String.class).noneMatch(Printable.predicate("==bye", "bye"::equals)).matcher(),
                asListOf(String.class).anyMatch(Printable.predicate("==bye", "bye"::equals)).matcher()
            )
        );
      }
    }

```

The example above will give you following output which you can easily examine with
your IDE's comparison window.

```
    expected:<[and:[
      collectionToList(x) allMatch[==bye]
      collectionToList(x) noneMatch[==bye]
      collectionToList(x) anyMatch[==bye]
    ]]> but was:<[when x=<[Hello, world]>; then and:[
      collectionToList(x) allMatch[==bye] was false because collectionToList(x)=<[Hello, world]>
      collectionToList(x) noneMatch[==bye]
      collectionToList(x) anyMatch[==bye] was false because collectionToList(x)=<[Hello, world]>
    ]->false]>

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
