public final class Foo {
    private int num;
    public void methodOne(final String txt, final boolean opt) {
        this.num += 1;
        this.methodTwo(txt, opt);
    }
    public void methodTwo(final String str, final boolean opt) {
        this.num += 1;
        this.methodOne(str, opt);
    }
}
