/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek;

import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cactoos.io.Directory;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.And;
import org.cactoos.scalar.Unchecked;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Matrix.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.6
 */
final class Matrix implements Iterable<Directive> {

    /**
     * Directory to save index to.
     */
    private final Path output;

    /**
     * Ctor.
     * @param target Target dir
     */
    Matrix(final Path target) {
        this.output = target;
    }

    @Override
    public Iterator<Directive> iterator() {
        final SortedMap<String, Map<String, String>> matrix = new TreeMap<>();
        new Unchecked<>(
            new And(
                path -> new And(
                    node -> {
                        final String name = String.format(
                            "%s.%s",
                            node.xpath("../../package/@id").get(0),
                            node.xpath("@id").get(0)
                        );
                        matrix.putIfAbsent(name, new TreeMap<>());
                        matrix.get(name).put(
                            node.xpath("/metric/title/text()").get(0),
                            node.xpath("@color").get(0)
                        );
                        return true;
                    },
                    new XMLDocument(path.toFile()).nodes("//class")
                ).value(),
                new Filtered<>(
                    path -> path.getFileName().toString().matches(
                        "^[A-Z].+\\.xml$"
                    ),
                    new Directory(this.output)
                )
            )
        ).value();
        return new Directives()
            .add("matrix")
            .append(new Header())
            .append(
                () -> new Directives()
                    .attr(
                        "xmlns:xsi",
                        "http://www.w3.org/2001/XMLSchema-instance"
                    )
                    .attr(
                        "xsi:noNamespaceSchemaLocation",
                        "xsd/matrix.xsd"
                    )
                    .iterator())
            .add("classes")
            .append(
                new Joined<>(
                    new Mapped<>(
                        ent -> new Directives().add("class").append(
                            new Joined<>(
                                new Mapped<>(
                                    mtd -> new Directives()
                                        .add("metric")
                                        .attr("name", mtd.getKey())
                                        .attr("color", mtd.getValue())
                                        .attr(
                                            "rank",
                                            Matrix.rank(mtd.getValue())
                                        )
                                        .up(),
                                    ent.getValue().entrySet()
                                )
                            )
                        ).attr("id", ent.getKey()).up(),
                        matrix.entrySet()
                    )
                )
            )
            .iterator();
    }

    /**
     * Rank of color.
     * @param color The color
     * @return Rank
     */
    private static int rank(final String color) {
        final int rank;
        if ("red".equals(color)) {
            rank = 1;
        } else if ("yellow".equals(color)) {
            rank = 3;
        } else {
            rank = 5;
        }
        return rank;
    }

}
