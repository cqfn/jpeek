public final class MethodMethodCalls {
  private int num;

  public int methodOne() {
    return this.num++;
  }

  public void methodTwo() {
    this.methodFour();
  }

  public void methodThree() {
    this.methodFour();
  }

  public int methodFour() {
    return this.num /= 2;
  }
}
