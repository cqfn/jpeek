public final class ClassUsesNotOwnAttr {

  private final String key;

  public ClassUsesNotOwnAttr(String key) {
    this.key = key;
  }

  public String getKey() {
    return new InnerKeyProvider().getKey();
  }

  private final class InnerKeyProvider {

    private String getKey() {
      return key;
    }
  }
}