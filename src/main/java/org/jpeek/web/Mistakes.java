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

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Table;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.cactoos.iterable.Mapped;
import org.jpeek.Version;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Futures for {@link AsyncReports}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class Mistakes {

    /**
     * Score multiplier.
     */
    private static final Double MULTIPLIER = 100000.0d;

    /**
     * DynamoDB table.
     */
    private final Table table;

    /**
     * Ctor.
     * @throws IOException If fails
     */
    Mistakes() throws IOException {
        this(new Dynamo().table("jpeek-mistakes"));
    }

    /**
     * Ctor.
     * @param tbl Table
     */
    Mistakes(final Table tbl) {
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
        for (final XML metric : index.nodes("//metric")) {
            this.add(
                metric.xpath("@name").get(0),
                Double.parseDouble(metric.xpath("@diff").get(0))
            );
        }
    }

    /**
     * Worst metrics.
     * @return List of them
     * @throws IOException If fails
     */
    public Iterable<Iterable<Directive>> worst() throws IOException {
        return new Mapped<>(
            item -> new Directives()
                .add("metric")
                .attr("id", item.get("metric").getS())
                .add("pos").set(item.get("pos").getN()).up()
                .add("neg").set(item.get("neg").getN()).up()
                .add("psum").set(
                    Double.parseDouble(item.get("psum").getN())
                        / Mistakes.MULTIPLIER
                ).up()
                .add("pavg").set(
                    Double.parseDouble(item.get("pavg").getN())
                        / Mistakes.MULTIPLIER
                ).up()
                .add("nsum").set(
                    Double.parseDouble(item.get("nsum").getN())
                        / Mistakes.MULTIPLIER
                ).up()
                .add("navg").set(
                    Double.parseDouble(item.get("navg").getN())
                        / Mistakes.MULTIPLIER
                ).up()
                .add("avg").set(
                    Double.parseDouble(item.get("avg").getN())
                        / Mistakes.MULTIPLIER
                ).up()
                .up(),
            this.table.frame()
                .where("version", new Version().value())
                .through(
                    new QueryValve()
                        .withScanIndexForward(false)
                        .withIndexName("mistakes")
                        .withConsistentRead(false)
                        // @checkstyle MagicNumber (1 line)
                        .withLimit(20)
                        .withSelect(Select.ALL_ATTRIBUTES)
                )
        );
    }

    /**
     * Add one metric.
     * @param name Metric name
     * @param diff The diff from XML
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void add(final String name, final double diff) throws IOException {
        final String version = new Version().value();
        final Iterator<Item> items = this.table.frame()
            .where("metric", name)
            .where("version", version)
            .iterator();
        final Item before;
        if (items.hasNext()) {
            before = items.next();
        } else {
            before = this.table.put(
                new Attributes()
                    .with("metric", name)
                    .with("version", version)
                    .with(
                        "ttl",
                        System.currentTimeMillis()
                            / TimeUnit.SECONDS.toMillis(1L)
                            // @checkstyle MagicNumber (1 line)
                            + TimeUnit.DAYS.toSeconds(100L)
                    )
                    .with("pos", 0L)
                    .with("psum", 0L)
                    .with("pavg", 0L)
                    .with("neg", 0L)
                    .with("nsum", 0L)
                    .with("navg", 0L)
                    .with("avg", 0L)
            );
        }
        if (diff > 0.0d) {
            before.put(
                new AttributeUpdates()
                    .with(
                        "pos",
                        new AttributeValueUpdate()
                            .withValue(new AttributeValue().withN("1"))
                            .withAction(AttributeAction.ADD)
                    )
                    .with(
                        "psum",
                        new AttributeValueUpdate()
                            .withAction(AttributeAction.ADD)
                            .withValue(
                                new AttributeValue().withN(
                                    Long.toString(
                                        (long) (diff * Mistakes.MULTIPLIER)
                                    )
                                )
                            )
                    )
            );
        } else {
            before.put(
                new AttributeUpdates()
                    .with(
                        "neg",
                        new AttributeValueUpdate()
                            .withValue(new AttributeValue().withN("1"))
                            .withAction(AttributeAction.ADD)
                    )
                    .with(
                        "nsum",
                        new AttributeValueUpdate()
                            .withAction(AttributeAction.ADD)
                            .withValue(
                                new AttributeValue().withN(
                                    Long.toString(
                                        (long) (-diff * Mistakes.MULTIPLIER)
                                    )
                                )
                            )
                    )
            );
        }
        final Item after = this.table.frame()
            .where("metric", name)
            .where("version", version)
            .iterator()
            .next();
        after.put(
            new AttributeUpdates()
                .with(
                    "navg",
                    new AttributeValueUpdate()
                        .withAction(AttributeAction.PUT)
                        .withValue(
                            new AttributeValue().withN(
                                Mistakes.div(
                                    Long.parseLong(after.get("nsum").getN()),
                                    Long.parseLong(after.get("neg").getN())
                                )
                            )
                        )
                )
                .with(
                    "pavg",
                    new AttributeValueUpdate()
                        .withAction(AttributeAction.PUT)
                        .withValue(
                            new AttributeValue().withN(
                                Mistakes.div(
                                    Long.parseLong(after.get("psum").getN()),
                                    Long.parseLong(after.get("pos").getN())
                                )
                            )
                        )
                )
        );
        final Item fin = this.table.frame()
            .where("metric", name)
            .where("version", version)
            .iterator()
            .next();
        // @checkstyle StringLiteralsConcatenationCheck (2 lines)
        final long avg = Long.parseLong(fin.get("pavg").getN())
            + Long.parseLong(fin.get("navg").getN()) >> 1;
        fin.put(
            new AttributeUpdates()
                .with(
                    "avg",
                    new AttributeValueUpdate()
                        .withAction(AttributeAction.PUT)
                        .withValue(
                            new AttributeValue().withN(
                                Long.toString(avg)
                            )
                        )
                )
        );
    }

    /**
     * Zero safe division.
     * @param head The head
     * @param div The div
     * @return Results in string
     */
    private static String div(final long head, final long div) {
        final long res;
        if (div == 0L) {
            res = 0L;
        } else {
            res = head / div;
        }
        return Long.toString(res);
    }

}
