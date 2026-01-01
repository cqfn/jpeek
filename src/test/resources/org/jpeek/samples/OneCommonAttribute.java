// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class OneCommonAttribute {
    private int num;

    public int methodOne() {
        return num++;
    }

    public int methodTwo() {
        return num--;
    }
}
