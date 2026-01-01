// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class Bar {
    private final Object key;
    private final Object value;
    private static String singleton = "";
    private static final String NAME = "hey";
    public Bar(final Object keyy, final Object val) {
        this.key = keyy;
        this.value = val;
        Bar.singleton = "hi";
    }
    public Object getKey() {
        Bar.singleton = "bye";
        Bar.NAME.length();
        return this.key;
    }
    public Object getValue() {
        Bar.NAME.length();
        return this.value;
    }
    public Object setValue(final Object val) {
        throw new UnsupportedOperationException("This object is immutable.");
    }
}
