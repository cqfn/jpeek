/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
public final class DocDistance {

    public int docThree() {
        final Temperature temp = new Temperature();
        final String txt = temp.toString();
        final String[] parts = txt.split(" ");
        final int t = Integer.parseInt(parts[0]);
        return t;
    }
}

final class Temperature {

    @Override
    public String toString() {
        return "10 C";
    }
}
