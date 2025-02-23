/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import com.jcabi.log.Logger;
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javassist.CtClass;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.scalar.AndInThreads;
import org.cactoos.scalar.Unchecked;
import org.jpeek.App;
import org.jpeek.Base;
import org.jpeek.Header;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Classes into XML.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.23
 */
public final class Skeleton {

    /**
     * Path to skeleton XSD schema.
     */
    private static final String SKELETON_XSD = "xsd/skeleton.xsd";

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        App.class.getResourceAsStream(Skeleton.SKELETON_XSD)
    );

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse The base
     */
    public Skeleton(final Base bse) {
        this.base = bse;
    }

    /**
     * As XML.
     * @return XML structure.
     */
    public XML xml() {
        final long start = System.currentTimeMillis();
        final XML xml = new StrictXML(
            new XMLDocument(
                new Xembler(
                    new Directives()
                        .add("skeleton")
                        .append(new Header())
                        .append(
                            () -> new Directives()
                                .attr("schema", Skeleton.SKELETON_XSD)
                                .iterator()
                        )
                        .add("app")
                        .attr("id", this.base)
                        .append(
                            new Joined<>(
                                new Mapped<>(
                                    ent -> new Directives()
                                        .add("package")
                                        .attr("id", ent.getKey())
                                        .append(ent.getValue())
                                        .up(),
                                    this.packages()
                                )
                            )
                        )
                ).xmlQuietly()
            ),
            Skeleton.SCHEMA
        );
        final long total = Long.parseLong(xml.xpath("count(//class)").get(0));
        final long time = System.currentTimeMillis() - start;
        if (total == 0L) {
            Logger.info(this, "No classes parsed in %[ms]s", time);
        } else {
            Logger.info(
                this, "%d bytecode classes parsed in %[ms]s (%[ms]s per class)",
                total, time, time / total
            );
        }
        return xml;
    }

    /**
     * Calculate Xembly for all packages.
     * @return XML for all packages (one by one)
     */
    @SuppressWarnings({
        "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.GuardLogStatement"
    })
    private Iterable<Map.Entry<String, Directives>> packages() {
        final long start = System.currentTimeMillis();
        final Collection<Map.Entry<String, Directives>> all =
            new CopyOnWriteArrayList<>();
        new Unchecked<>(
            new AndInThreads(
                new Mapped<>(
                    clz -> () -> all.add(Skeleton.xembly(clz)),
                    new Classes(this.base)
                )
            )
        ).value();
        final Map<String, Directives> map = new HashMap<>(0);
        for (final Map.Entry<String, Directives> ent : all) {
            map.putIfAbsent(ent.getKey(), new Directives());
            map.get(ent.getKey()).append(ent.getValue());
        }
        Logger.debug(
            this, "%d classes parsed via ASM in %[ms]s",
            map.size(), System.currentTimeMillis() - start
        );
        return map.entrySet();
    }

    /**
     * Calculate Xembly for a single .class file.
     * @param ctc The class
     * @return Metrics
     */
    private static Map.Entry<String, Directives> xembly(final CtClass ctc) {
        ctc.defrost();
        String pkg = ctc.getPackageName();
        if (pkg == null) {
            pkg = "";
        }
        return new MapEntry<>(
            pkg,
            new Directives()
                .add("class")
                .comment(
                    Xembler.escape(
                        String.format(
                            "Package: %s; name: %s; file: %s",
                            ctc.getPackageName(),
                            ctc.getName(),
                            ctc.getClassFile().getName()
                        )
                    )
                )
                .attr("id", ctc.getSimpleName())
                .append(new XmlClass(ctc))
                .up()
        );
    }

}
