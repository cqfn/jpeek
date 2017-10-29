public class Bar {

    private final Object key;

    private final Object value;

    public Bar(final Object keyy, final Object val) {
        super();
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