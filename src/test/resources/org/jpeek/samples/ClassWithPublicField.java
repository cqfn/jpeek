package org.jpeek.samples;

public class ClassWithPublicField {
    public static String NAME = "hey";

    public void test() {
        ClassWithPublicField.NAME = "test";
    }
}
