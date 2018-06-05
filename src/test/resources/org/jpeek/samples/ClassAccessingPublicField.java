package org.jpeek.samples;

public final class ClassAccessingPublicField {
    public static String NAME = "hey";

    public void test() {
        ClassWithPublicField.NAME = "test";
    }
}