/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.ScanValve;
import com.jcabi.dynamo.Table;
import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
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
 * @since 0.8
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class Results {

    /**
     * DynamoDB table.
     */
    private final Table table;

    /**
     * Ctor.
     */
    Results() {
        this(new Dynamo().table("jpeek-results"));
    }

    /**
     * Ctor.
     * @param tbl Table
     */
    Results(final Table tbl) {
        this.table = tbl;
    }

    /**
     * Delete them all.
     * @return How many were deleted
     */
    public int flush() {
        final Iterator<Item> items = this.table.frame().iterator();
        int total = 0;
        while (items.hasNext()) {
            items.next();
            items.remove();
            ++total;
        }
        return total;
    }

    /**
     * Add result.
     * @param artifact The artifact, like "org.jpeek:jpeek"
     * @param dir Directory with files
     * @throws IOException If fails
     */
    public void add(final String artifact, final Path dir)
        throws IOException {
        final XML index = new XMLDocument(
            dir.resolve("index.xml").toFile()
        );
        final int elements = Integer.parseInt(
            index.xpath("max(/index/metric/elements/number(text()))").get(0)
        );
        final Number diff = new DyNum(index.xpath("/index/@diff").get(0));
        final long score = new DyNum(
            index.xpath("/index/@score").get(0)
        ).longValue();
        final long rank = (long) ((double) score * (1.0d - diff.doubleValue()));
        if (elements < 100) {
            Logger.info(
                this, "%d elements NOT saved for %s by %s, rank=%d, score=%d, metrics=%d",
                elements, artifact, new Version().value(), rank, score,
                Integer.parseInt(index.xpath("count(/index/metric)").get(0))
            );
        } else {
            this.table.put(
                new Attributes()
                    .with("good", "true")
                    .with("artifact", artifact)
                    .with("rank", rank)
                    .with("score", score)
                    .with("diff", diff.longValue())
                    .with(
                        "defects",
                        new DyNum(
                            index.xpath("/index/@defects").get(0)
                        ).longValue()
                    )
                    .with("elements", elements)
                    .with(
                        "classes",
                        Integer.parseInt(
                            index.xpath(
                                "/index/metric[1]/classes/text()"
                            ).get(0)
                        )
                    )
                    .with("version", new Version().value())
                    .with("added", System.currentTimeMillis())
                    .with(
                        "ttl",
                        System.currentTimeMillis()
                            / TimeUnit.SECONDS.toMillis(1L)
                            + TimeUnit.DAYS.toSeconds(100L)
                    )
            );
            Logger.info(
                this, "%d elements saved for %s by %s, rank=%d, score=%d",
                elements, artifact, new Version().value(), rank, score
            );
        }
    }

    /**
     * Has this artifact?
     * @param artifact The artifact, e.g. "org.jpeek:jpeek"
     * @return TRUE if it exists
     */
    public boolean exists(final String artifact) {
        return !this.table.frame()
            .where("good", "true")
            .where("artifact", artifact)
            .isEmpty();
    }

    /**
     * Get score of this.
     * @param artifact The artifact, e.g. "org.jpeek:jpeek"
     * @return The score
     * @throws IOException If fails
     */
    public double score(final String artifact) throws IOException {
        final Item item = this.table.frame()
            .where("good", "true")
            .where("artifact", artifact)
            .iterator()
            .next();
        return new DyNum(item, "score").doubleValue();
    }

    /**
     * Recent artifacts..
     * @return List of them
     */
    public Iterable<Iterable<? extends Directive>> recent() {
        return new Mapped<>(
            item -> {
                final String[] parts = item.get("artifact").getS().split(":");
                return new Directives()
                    .add("repo")
                    .add("group").set(parts[0]).up()
                    .add("artifact").set(parts[1]).up()
                    .up();
            },
            this.table.frame()
                .where("good", "true")
                .through(
                    new QueryValve()
                        .withScanIndexForward(false)
                        .withIndexName("recent")
                        .withConsistentRead(false)
                        .withLimit(25)
                        .withAttributesToGet("artifact")
                )
        );
    }

    /**
     * All of them.
     * @return List of them
     */
    public Iterable<Iterable<? extends Directive>> all() {
        return new Mapped<>(
            item -> {
                final String[] parts = item.get("artifact").getS().split(":");
                return new Directives()
                    .add("repo")
                    .add("version").set(item.get("version").getS()).up()
                    .add("added")
                    .set(
                        Instant.ofEpochMilli(
                            Long.parseLong(item.get("added").getN())
                        )
                        .atZone(ZoneOffset.UTC)
                        .toLocalDateTime()
                        .toString()
                    )
                    .up()
                    .add("group").set(parts[0]).up()
                    .add("artifact").set(parts[1]).up()
                    .add("rank")
                    .set(new DyNum(item, "rank").doubleValue())
                    .up()
                    .add("score")
                    .set(new DyNum(item, "score").doubleValue())
                    .up()
                    .add("defects")
                    .set(new DyNum(item, "defects").doubleValue())
                    .up()
                    .add("classes")
                    .set(Integer.parseInt(item.get("classes").getN()))
                    .up()
                    .add("elements")
                    .set(Integer.parseInt(item.get("elements").getN()))
                    .up()
                    .up();
            },
            this.table.frame()
                .where(
                    "elements",
                    new Condition()
                        .withAttributeValueList(
                            new AttributeValue().withN("99")
                        )
                        .withComparisonOperator(ComparisonOperator.GT)
                )
                .through(
                    new ScanValve()
                        .withLimit(1000)
                        .withAttributeToGet(
                            "artifact", "classes", "defects", "version",
                            "rank", "score", "elements", "added"
                        )
                )
        );
    }

    /**
     * Best artifacts.
     * @return List of them
     * @throws IOException If fails
     */
    public Iterable<Iterable<Directive>> best() throws IOException {
        return new Mapped<>(
            item -> {
                final String[] parts = item.get("artifact").getS().split(":");
                return new Directives()
                    .add("repo")
                    .add("group").set(parts[0]).up()
                    .add("artifact").set(parts[1]).up()
                    .add("rank")
                    .set(new DyNum(item, "rank").doubleValue())
                    .up()
                    .add("score")
                    .set(new DyNum(item, "score").doubleValue())
                    .up()
                    .add("diff")
                    .set(new DyNum(item, "diff").doubleValue())
                    .up()
                    .add("defects")
                    .set(new DyNum(item, "defects").doubleValue())
                    .up()
                    .add("classes")
                    .set(Integer.parseInt(item.get("classes").getN()))
                    .up()
                    .add("elements")
                    .set(Integer.parseInt(item.get("elements").getN()))
                    .up()
                    .up();
            },
            this.table.frame()
                .where("version", new Version().value())
                .through(
                    new QueryValve()
                        .withScanIndexForward(false)
                        .withIndexName("ranks")
                        .withConsistentRead(false)
                        .withLimit(20)
                        .withAttributesToGet(
                            "artifact", "score", "diff", "defects",
                            "classes", "elements", "rank"
                        )
                )
        );
    }

}
