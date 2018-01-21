public final class MethodsWithDiffParamTypes {

  private int num;

  public <T> double methodOne(final T src) {
    ++num;
    return num * 2.4d;
  }

  public void methodTwo(final long count) {
    ++num;
  }

  public char methodThree(final String src, final int count) {
    ++num;
    return src.charAt(num * (count - 1));
  }

  public int methodFour(final java.util.List<String> l) {
    return l.size();
  }

  public int[] methodFive(final Integer[] arr) {
    return new int[] { arr.length };
  }

  public java.util.Date methodSix(final java.sql.Timestamp ts) {
    return new java.util.Date(ts.getTime());
  }
}
