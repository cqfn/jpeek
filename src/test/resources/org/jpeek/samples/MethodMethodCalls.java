// SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class MethodMethodCalls {
    private int num;

    public int methodOne() {
        return num++;
    }

    public int methodTwo() {
        return this.methodOne();
    }

    public int methodThree() {
        return num--;
    }

    public int methodFour() {
        return this.methodTwo();
    }

    public int methodFive() {
        throw new UnsupportedOperationException();
    }
}
