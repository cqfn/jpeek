public final class Bar {
    private Object key;
    private Object value;
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
