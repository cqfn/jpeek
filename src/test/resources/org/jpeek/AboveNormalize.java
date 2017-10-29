public final class AboveNormalize {
    private final Object key;
    private final Object value;
    public AboveNormalize(final Object keyy, final Object val) {
        this.key = keyy;
        this.value = val;
    }
    public Object getKey() {
        return this.key;
    }
    public Object getValue() {
        return this.value;
    }
    public Object setValue(final Object val) {
        throw new UnsupportedOperationException("This object is immutable.");
    }
}
