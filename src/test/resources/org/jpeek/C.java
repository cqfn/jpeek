public class C {
    private int a1, a2;

    public void m1(int x) {
        this.a1 = x;
    }

    public void m2(int y) {
        this.a2 = y;
    }

    public int m3() {
        return this.a1 + this.a2;
    }

    public void m4() {
        this.m1(0);
    }
}