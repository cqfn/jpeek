public final class TwoCommonAttributes {
    private int first;
    private String second;
    private int third;

    public void methodOne(final String txt, final boolean opt) {
        this.first += 1;
        this.methodTwo(txt, opt);
    }

    public void methodTwo(final String str, final boolean opt) {
        this.second = "test";
    }

    public void methodThree() {
        this.third = 6;
    }
}
