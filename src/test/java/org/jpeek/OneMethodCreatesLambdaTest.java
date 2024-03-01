package org.jpeek;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jpeek.samples.OneMethodCreatesLambda;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class OneMethodCreatesLambdaTest {

    // Test with positive numbers
    @Test
    public void testDoSomethingWithPositiveNumbers() {
        OneMethodCreatesLambda instance = new OneMethodCreatesLambda();
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<Double> expected = Arrays.asList(3.0, 6.0, 9.0);
        List<Double> result = instance.doSomething(input);
        
        assertEquals(expected, result, "The doSomething method should correctly multiply each element by 3.");
    }

    // Test with an empty list
    @Test
    public void testDoSomethingWithEmptyList() {
        OneMethodCreatesLambda instance = new OneMethodCreatesLambda();
        List<Integer> input = Collections.emptyList();
        List<Double> expected = Collections.emptyList();
        List<Double> result = instance.doSomething(input);
        
        assertEquals(expected, result, "The doSomething method should return an empty list when input is empty.");
    }

    // Test with negative numbers
    @Test
    public void testDoSomethingWithNegativeNumbers() {
        OneMethodCreatesLambda instance = new OneMethodCreatesLambda();
        List<Integer> input = Arrays.asList(-1, -2, -3);
        List<Double> expected = Arrays.asList(-3.0, -6.0, -9.0);
        List<Double> result = instance.doSomething(input);
        
        assertEquals(expected, result, "The doSomething method should correctly handle negative numbers.");
    }

    // Test with a mix of positive, negative, and zero
    @Test
    public void testDoSomethingWithMixedNumbers() {
        OneMethodCreatesLambda instance = new OneMethodCreatesLambda();
        List<Integer> input = Arrays.asList(-1, 0, 1);
        List<Double> expected = Arrays.asList(-3.0, 0.0, 3.0);
        List<Double> result = instance.doSomething(input);
        
        assertEquals(expected, result, "The doSomething method should correctly handle a mix of negative, zero, and positive numbers.");
    }

    // Test with large numbers
    @Test
    public void testDoSomethingWithLargeNumbers() {
        OneMethodCreatesLambda instance = new OneMethodCreatesLambda();
        List<Integer> input = Arrays.asList(Integer.MAX_VALUE, Integer.MIN_VALUE);
        List<Double> expected = Arrays.asList(3.0 * Integer.MAX_VALUE, 3.0 * Integer.MIN_VALUE);
        List<Double> result = instance.doSomething(input);
        
        assertEquals(expected, result, "The doSomething method should correctly handle large numbers without overflow.");
    }
}
