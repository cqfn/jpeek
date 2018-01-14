public final class MethodsWithDiffParamTypes {

  private int num;

  public double methodOne(final String src) {
    ++num;
    return num * 2.4d;
  }

  public double methodTwo(final long count) {
    ++num;
    return num * count;
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