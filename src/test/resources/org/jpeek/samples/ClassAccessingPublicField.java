// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

package org.jpeek.samples;

public final class ClassAccessingPublicField {
    public static String NAME = "hey";

    public void test() {
        ClassWithPublicField.NAME = "test";
    }
}
