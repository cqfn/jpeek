public final class OverrideGenericMethodFromInterface implements GenericIncrementor1<Integer> {

  private final int num;

  public OverrideGenericMethodFromInterface(int num) {
    this.num = num;
  }

  @Override
  public Integer inc() {
    return num + 1;
  }
}

interface GenericIncrementor1<T> {
  T inc();
}