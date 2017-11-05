public final class OverrideGenericMethodFromClass extends Parent<String> {

  private final StringBuilder log;

  public OverrideGenericMethodFromClass() {
    super(new String[10]);
    this.log = new StringBuilder();
  }

  @Override
  void insert(final int index, final String item) {
    log
        .append("Insert ").append(item)
        .append(" at index ").append(index);

    storage[index] = item;
  }
}

class Parent<T> {

  public final T[] storage;

  protected Parent(final T[] storage) {
    this.storage = storage;
  }

  void insert(final int index, final T item) {
    storage[index] = item;
  }
}