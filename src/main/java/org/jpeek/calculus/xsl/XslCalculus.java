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
package org.jpeek.calculus.xsl;

import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.Map;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.jpeek.calculus.Calculus;

/**
 * Metrics xsl calculus. Use an xsl sheet to transform the input skeleton into
 * the xml containing the calculation.
 * @since 0.30.9
 */
public final class XslCalculus implements Calculus {

    /**
     * Name of the metric this Calculus is for.
     */
    private final String metric;

    /**
     * Ctor.
     * @param metric Name of the metric this Calculus is for.
     */
    public XslCalculus(final String metric) {
        this.metric = metric;
    }

    @Override
    public XML node(
        final Map<String, Object> params, final XML skeleton
    ) throws IOException {
        return new XSLDocument(
            new TextOf(
                new ResourceOf(
                    new FormattedText("org/jpeek/metrics/%s.xsl", this.metric)
                )
            ).asString(),
            Sources.DUMMY,
            params
        ).transform(skeleton);
    }

}
