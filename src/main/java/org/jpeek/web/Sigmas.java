/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.jpeek.Version;

/**
 * Mu and sigma for best metrics.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.17
 */
final class Sigmas {

    /**
     * DynamoDB table.
     */
    private final Table table;

    /**
     * Ctor.
     */
    Sigmas() {
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
        if (defects < 0.15d && classes > 200) {
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
        final Item item;
        final Iterator<Item> items = this.table.frame()
            .through(
                new QueryValve()
                    .withLimit(1)
                    .withSelect(Select.ALL_ATTRIBUTES)
            )
            .where("metric", metric.xpath("@name").get(0))
            .where("version", new Version().value())
            .iterator();
        if (items.hasNext()) {
            item = items.next();
        } else {
            item = this.table.put(
                new Attributes()
                    .with("metric", metric.xpath("@name").get(0))
                    .with("version", new Version().value())
                    .with("artifact", "?")
                    .with("champions", 0L)
                    .with("mean", new DyNum(0.5d).longValue())
                    .with("sigma", new DyNum(0.1d).longValue())
            );
        }
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
                    .with("artifact", metric.xpath("/index/@artifact").get(0))
                    .with(
                        "champions",
                        new AttributeValueUpdate()
                            .withValue(new AttributeValue().withN("1"))
                            .withAction(AttributeAction.ADD)
                    )
                    .with("mean", new DyNum(mean).update())
                    .with("sigma", new DyNum(sigma).update())
            );
        }
    }

}
