public final class OnlyOneMethodWithParams {

  private int num;

  public OnlyOneMethodWithParams(int num) {
    this.num = num;
  }

  public double doSomething(final long count) {
    return 3.0d * (num + count);
  }
}