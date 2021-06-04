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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Represents metric information.
 *
 * @since 1.0.0
 */
@SuppressWarnings("PMD.ConstructorShouldDoInitialization")
public class MetricPresentation {

    /**
     * File with metric results.
     */
    private final Document doc;

    /**
     * Contains values for classes.
     */
    private final Map<String, String> values = new HashMap<>();

    /**
     * Initializes object of metric representation.
     *
     * @param path Path to metric file
     * @throws IOException if fails
     */
    @SuppressWarnings({
        "PMD.ConstructorOnlyInitializesOrCallOtherConstructors",
        "PMD.ConstructorShouldDoInitialization"
    })
    public MetricPresentation(final String path) throws IOException {
        final File input = new File(path);
        this.doc = Jsoup.parse(input, "UTF-8");
        this.parseValues();
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
     * Gets classes values from metric file.
     */
    private void parseValues() {
        final Element table = this.doc.select("table").get(0);
        final Elements rows = table.select("tr");
        for (int ind = 1; ind < rows.size(); ind += 1) {
            String label = rows.get(ind).select("td").get(0).select("code").get(0).text();
            label = label.substring(label.lastIndexOf('.') + 1);
            final String value = rows.get(ind).select("td").get(1).text();
            this.values.put(label, value);
        }
    }
}

