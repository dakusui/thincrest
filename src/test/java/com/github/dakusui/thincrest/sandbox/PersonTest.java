package com.github.dakusui.thincrest.sandbox;

import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.api.SoftAssertions;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.*;

public class PersonTest {

    @Test(expected = ComparisonFailure.class)
    public void testNameAndAge() {
        Person person = new Person("alice", 30);

        assertThat(person.getName())
                .isNotNull()
                .isEqualTo("Alice")
                .startsWith("Al")
                .endsWith("ce")
                .contains("lic");

        assertThat(person.getAge())
                .isGreaterThan(20)
                .isLessThanOrEqualTo(30);
    }

    @Test
    public void testList() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        assertThat(names)
                .hasSize(3)
                .contains("Alice", "Bob")
                .doesNotContain("David");
    }

    @Test(expected = SoftAssertionError.class)
    public void testNameAndAgeUsingSoftAssertions() {
        Person person = new Person("Alicia", 35);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(person.getName())
                .isEqualTo("Alice");

        softly.assertThat(person.getAge())
                .isLessThanOrEqualTo(40);

        // Report all collected errors
        softly.assertAll();
    }
}