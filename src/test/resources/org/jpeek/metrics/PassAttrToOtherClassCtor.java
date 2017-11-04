public final class PassAttrToOtherClassCtor {

  private final int num1;

  public PassAttrToOtherClassCtor(int num1) {
    this.num1 = num1;
  }

  public String asJson() {
    return new AsJson(this.num1 + 3).asJson();
  }

  
  public static final class AsJson {

    private final int num2;

    public AsJson(int num2) {
      this.num2 = num2;
    }

    public String asJson() {
      return String.format("{\"num\": %d}", this.num2);
    }
  }
}