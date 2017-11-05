public final class OverrideGenericMethodsFromTwoInterfaces
    implements GenericIncrementor3<Integer>, GenericDecrementor3<Integer> {

  private final int num;

  public OverrideGenericMethodsFromTwoInterfaces(int num) {
    this.num = num;
  }

  @Override
  public Integer inc() {
    return num + 1;
  }

  @Override
  public Integer dec() {
    return num - 1;
  }
}

interface GenericIncrementor3<T> {
  T inc();
}

interface GenericDecrementor3<T> {
  T dec();
}