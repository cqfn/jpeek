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
package org.jpeek.calculus;

import com.jcabi.xml.XMLDocument;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents metric information.
 *
 * @since 1.0.0
 */
@SuppressWarnings({"PMD.ConstructorShouldDoInitialization", "PMD.AvoidDuplicateLiterals"})
public class MetricPresentation {

    /**
     * XML file with metric results.
     */
    private final XMLDocument xml;

    /**
     * Contains values for classes.
     */
    private final Map<String, String> values = new HashMap<>();

    /**
     * Initializes object of metric representation.
     *
     * @param path Path to metric XML file
     * @throws FileNotFoundException if fails
     */
    public MetricPresentation(final String path) throws FileNotFoundException {
        this.xml = new XMLDocument(
            new File(path)
        );
    }

    /**
     * Getter for value.
     *
     * @return Values
     */
    public Map<String, String> getValues() {
        return this.values;
    }

    /**
     * Initializes object of metric representation and parses values.
     *
     * @param path Path to metric file
     * @return Instance of metric representation
     * @throws IOException if fails
     */
    static MetricPresentation initialize(final String path) throws IOException {
        final MetricPresentation metpres = new MetricPresentation(path);
        metpres.parseValues();
        return metpres;
    }

    /**
     * Gets classes values from metric file.
     */
    private void parseValues() {
        for (final String clazz : this.xml.xpath("//class/@id")) {
            final String value = this.xml.xpath(
                String.format("//class[@id=\"%s\"]/@value", clazz)
            ).get(0);
            this.values.put(clazz, value);
        }
    }
}

