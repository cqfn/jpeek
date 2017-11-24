/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package org.jpeek.web;

import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Table;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Path;
import org.jpeek.Version;

/**
 * Mu and sigma for best metrics.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.17
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class Sigmas {

    /**
     * DynamoDB table.
     */
    private final Table table;

    /**
     * Ctor.
     * @throws IOException If fails
     */
    Sigmas() throws IOException {
        this(new Dynamo().table("jpeek-mistakes"));
    }

    /**
     * Ctor.
     * @param tbl Table
     */
    Sigmas(final Table tbl) {
        this.table = tbl;
    }

    /**
     * Add result.
     * @param dir Directory with files
     * @throws IOException If fails
     */
    public void add(final Path dir) throws IOException {
        final XML index = new XMLDocument(
            dir.resolve("index.xml").toFile()
        );
        final double defects = Double.parseDouble(
            index.xpath("/index/@defects").get(0)
        );
        final int classes = Integer.parseInt(
            index.xpath("/index/metric[1]/classes/text()").get(0)
        );
        // @checkstyle MagicNumber (1 line)
        if (defects < 0.1d && classes > 100) {
            for (final XML metric : index.nodes("//metric")) {
                this.add(metric);
            }
        }
    }

    /**
     * Add one metric.
     * @param metric XML with metric
     * @throws IOException If fails
     */
    private void add(final XML metric) throws IOException {
        final Item item = this.table.frame()
            .through(
                new QueryValve()
                    .withLimit(1)
                    .withSelect(Select.ALL_ATTRIBUTES)
            )
            .where("metric", metric.xpath("@name").get(0))
            .where("version", new Version().value())
            .iterator()
            .next();
        final double mean = Double.parseDouble(
            metric.xpath("mean/text()").get(0)
        );
        final double sigma = Double.parseDouble(
            metric.xpath("sigma/text()").get(0)
        );
        final boolean reverse = Boolean.parseBoolean(
            metric.xpath("reverse/text()").get(0)
        );
        final double mbefore = new DyNum(item, "mean").doubleValue();
        final double sbefore = new DyNum(item, "sigma").doubleValue();
        // @checkstyle BooleanExpressionComplexityCheck (1 line)
        if (sigma < sbefore || mean < mbefore && reverse
            || mean > mbefore && !reverse) {
            item.put(
                new AttributeUpdates()
                    .with("mean", new DyNum(mean).update())
                    .with("sigma", new DyNum(sigma).update())
            );
        }
    }

}
