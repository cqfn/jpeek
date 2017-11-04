public final class OverrideMethodFromInterface implements Incrementor {

  private final int num;

  public OverrideMethodFromInterface(int num) {
    this.num = num;
  }

  @Override
  public Integer inc() {
    return num + 1;
  }
}

interface Incrementor {
  Integer inc();
}