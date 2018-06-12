public final class TwoCommonMethods {

  public int methodOne(final int i) {
    return i * 2;
  }

  public int methodTwo() {
    return methodOne(2);
  }

  public int methodThree() {
    return methodOne(3);
  }

  public double methodFour(final double d) {
    return d * 2;
  }

  public double methodFive() { return methodFour(5); }

  public double methodSix() { return methodFour(6); }

}
