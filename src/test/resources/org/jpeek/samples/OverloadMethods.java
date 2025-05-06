// SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class OverloadMethods {

  private int num;

  public double methodOne(final String src) {
    ++num;
    return methodOne(src, "");
  }

  public double methodOne(final String src, final String dst) {
    ++num;
    return methodOne(src, dst, false);
  }

  public double methodOne(final String src, final String dst, final boolean opt) {
    ++num;
    return methodOne(src, dst, false, 0);
  }

  public double methodOne(final String src, final String dst, final boolean opt, final double count) {
    ++num;
    return num + src.length() + dst.length() + count;
  }
}
