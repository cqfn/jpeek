// SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class IndirectlyRelatedPairs {
    int a, b, c, d;
    public IndirectlyRelatedPairs(final int x) {
        methodOne(a+d);
    }
    public void methodOne(final int x) {
        methodTwo(a+b);
    }
    public void methodTwo(final int x) {
        methodThree(b+c);
    }
    public void methodThree(final int x) {
        new IndirectlyRelatedPairs(c+d);
    }
}
