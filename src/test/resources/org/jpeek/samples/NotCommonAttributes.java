// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class NotCommonAttributes {
    private int num;
    private int anotherNum;

    public int methodOne() {
        return num++;
    }

    public int methodTwo() {
        return anotherNum--;
    }
}
