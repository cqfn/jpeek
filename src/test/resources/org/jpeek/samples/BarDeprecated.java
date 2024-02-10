package org.jpeek.samples;

@Deprecated
public class BarDeprecated {
    private final Object key;
    @Deprecated
    public final Object value;
    private static String singleton = "";
    @Deprecated
    private static final String NAME = "hey";
    public BarDeprecated(final Object keyy, final Object val) {
        this.key = keyy;
        this.value = val;
        BarDeprecated.singleton = "hi";
    }
    @Deprecated
    public Object getKey() {
        BarDeprecated.singleton = "bye";
        BarDeprecated.NAME.length();
        return this.key;
    }
    public Object getValue() {
        BarDeprecated.NAME.length();
        return this.value;
    }
    public Object setValue(final Object val) {
        throw new UnsupportedOperationException("This object is immutable.");
    }
}