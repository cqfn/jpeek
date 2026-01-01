// SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
// SPDX-License-Identifier: MIT

public final class OnlyOneMethodWithParams {

  private int num;

  public OnlyOneMethodWithParams(int num) {
    this.num = num;
  }

  public double doSomething(final long count) {
    return 3.0d * (num + count);
  }
}
