# thincrest
When you are testing a Java class ```BankAccount```, you probably write a test scenario like following.

```java
    BankAccount bankAccount = new BankAccount("John Doe");
    bankAccount.deposit(1000);
    bankAccount.withdraw(110);
```

Don't you think that it's cool if you can get a comparison window like this?
![screenshot from 2018-09-06 03-39-02](https://user-images.githubusercontent.com/529265/45114161-32748b00-b187-11e8-8cf1-134771092a87.png)

```thincrest``` library is for that. 

You can find more on this example in [Tutorial](https://github.com/dakusui/thincrest/wiki/Tutorial)

**thincrest**(pronounced 'think rest') is a small library that does assertions
 such as **Hamcrest**[[1]] and **Assert J**[[2]]. It is designed to be able to
  balance following values
  
 * Readable messages on failures.
 * Readable test codes.
 * Avoid fail->fix->run->fail->fix->run loop.
 * Easy to use with various classes without writing custom matchers, although it is still possible.
 * IDE friendliness.
 
It used to be a thin wrapper for **Hamcrest** ant it is the reason why this library
was initially named so, but it is not anymore.

# Usage
## Requirements
**thincrest** requires Java SE8 or later. 

## Installation
You can use following maven coordinate.

```xml
<dependency>
  <groupId>com.github.dakusui</groupId>
  <artifactId>thincrest</artifactId>
  <version>{VERSION}</version>
</dependency>
```

Replace ```{VERSION}``` with one that you are going to use such as ```3.5.0```. 
You can check available versions from [here](https://search.maven.org/search?q=a:thincrest)(The Central Repository).
All released versions are listed [here](https://github.com/dakusui/thincrest/releases)(GitHub).

# Known issues
## A comparison window of IntelliJ gives broken information
This behavior was found as [Issue-19](https://github.com/dakusui/thincrest/issues/19) of ```thincrest```
If a comparison window of IntelliJ gives broken information like following,
![screen shot 2018-08-24 at 5 43 46 am](https://user-images.githubusercontent.com/529265/44551042-f130b400-a760-11e8-8d22-436236fc1079.png)

this behavior is caused by an IntelliJ side's bug described as [IDEA-193010](https://youtrack.jetbrains.com/issue/IDEA-193010).
Use Intellij not having this bug (previous to 2018.1 or newer than it) is the solution.
Or you can print the strings (```message```, ```expected```, and ```actual``` that you are passing to ```ExecutionFailure```'s constructor) would be a workaround.

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
