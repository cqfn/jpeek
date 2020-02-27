/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jpeek;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasValues;

/**
 * Tests for {@link ReportData}.
 * @since 0.30.9
 */

public final class ReportDataTest {

	/**
	 * Sample map for tests.
	 */
	private static final Map<String, Object> ARGS = new MapOf<>(
        new MapEntry<>("a", 1), new MapEntry<>("b", 2)
    );

    @Test
    public void reportsData() throws Exception {
        final String name = "whatever";
        final Random random = new Random();
        final double mean = random.nextDouble();
        final double sigma = random.nextDouble();
        final ReportData data = new ReportData(name, ReportDataTest.ARGS, mean, sigma);
        new Assertion<>("Must returns name", data.metric(), new IsEqual<>(name)).affirm();
        new Assertion<>("Must returns mean", data.mean(), new IsEqual<>(mean)).affirm();
        new Assertion<>("Must returns sigma", data.sigma(), new IsEqual<>(sigma)).affirm();
        new Assertion<>(
            "Must returns args",
            data.params().entrySet(),
            new HasValues<>(ReportDataTest.ARGS.entrySet())
        ).affirm();
    }

    @Test
    public void shouldBeImmutable() throws Exception {
        final String name = "metric";
        final int size = ReportDataTest.ARGS.size();
        final Map<String, Object> params = new HashMap<>(ReportDataTest.ARGS);
        final ReportData data = new ReportData(name, params);
        params.clear();
        data.params().clear();
        new Assertion<>(
            "Must be immutable",
            data.params().entrySet().size(),
            new IsEqual<>(size)
        ).affirm();
    }
}
