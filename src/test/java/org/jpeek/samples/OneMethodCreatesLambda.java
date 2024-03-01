package org.jpeek.samples;
import java.util.List;
import java.util.stream.Collectors;

public final class OneMethodCreatesLambda {

  private int num;

  public List<Double> doSomething(final List<Integer> l) {
    return l
        .stream()
        .map(i -> 3.0d * i)
        .collect(Collectors.toList());
  }
}