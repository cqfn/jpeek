public final class OverrideGenericMethodsFromInterfacesInheritance implements GenericOperations2<Integer> {

  private final int num;

  public OverrideGenericMethodsFromInterfacesInheritance(int num) {
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

interface GenericIncrementor2<T> {
  T inc();
}

interface GenericDecrementor2<T> {
  T dec();
}

interface GenericOperations2<T> extends GenericIncrementor2<T>, GenericDecrementor2<T> {

}