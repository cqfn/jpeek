public final class ThreeMethodsAccessThreeAttrs {

  private final int f1;
  private final int f2;
  private final int f3;
  private final int f4;

  public ThreeMethodsAccessThreeAttrs(int f1, int f2, int f3, int f4) {
    this.f1 = f1;
    this.f2 = f2;
    this.f3 = f3;
    this.f4 = f4;
  }

  public int methodOne() {
    return f1 + f2 + f3;
  }

  public int methodTwo() {
    return f3 + f1 + f2;
  }

  public int methodThree() {
    return f2 + f3 + f1;
  }

  public int methodFour() {
    return 0;
  }
}