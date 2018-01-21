public final class NoDisagreements {
    private Object key;
    private Object value;
    public NoDisagreements(final Object keyy, final Object val) {
        this.key = keyy;
        this.value = val;
    }
    public Object getKey(Object one, Object two) {
        return this.key;
    }
    public Object getValue(Object three) {
        return this.value;
    }
    public Object setValue(final Object val) {
        final Object old = this.value;
        this.value = val;
        return old;
    }
}

