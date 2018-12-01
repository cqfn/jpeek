public final class NotCommonAttributesWithAllArgsConstructor {
    private int num;
    private int anotherNum;

    public NotCommonAttributesWithAllArgsConstructor(int num, int anotherNum) {
        this.num = num;
        this.anotherNum = num;
    }

    public int methodOne() {
        return num++;
    }

    public int methodTwo() {
        return anotherNum--;
    }
}
