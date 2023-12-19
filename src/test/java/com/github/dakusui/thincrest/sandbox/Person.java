package com.github.dakusui.thincrest.sandbox;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}