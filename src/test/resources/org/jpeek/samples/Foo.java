// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class Foo {
    private int num;
    public void methodOne(final String txt, final boolean opt) {
        this.num += 1;
        this.methodTwo(txt, opt);
    }
    public void methodTwo(final String str, final boolean opt) {
        this.num += 1;
        this.methodOne(str, opt);
    }
}
