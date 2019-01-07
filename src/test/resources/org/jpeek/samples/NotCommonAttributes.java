public final class NotCommonAttributes {
    private int num;
    private int anotherNum;

    public int methodOne() {
        return num++;
    }

    public int methodTwo() {
        return anotherNum--;
    }
}
