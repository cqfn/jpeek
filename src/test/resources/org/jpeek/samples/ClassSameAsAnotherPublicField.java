// SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
// SPDX-License-Identifier: MIT

package org.jpeek.samples;

public final class ClassSameAsAnotherPublicField {
    private String NAME = "hey";

    public void test() {
        ClassWithPublicField.NAME = "test";
    }

    public void foo() {
        this.NAME = "test";
    }
}

class ClassWithPublicField {
    public static String NAME = "yoo";
}
