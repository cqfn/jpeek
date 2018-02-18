/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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
import org.cactoos.collection.Joined;
import org.cactoos.collection.Mapped;
import org.cactoos.map.MapEntry;
import org.cactoos.scalar.AndInThreads;
import org.cactoos.scalar.UncheckedScalar;
import org.jpeek.App;
import org.jpeek.Base;
import org.jpeek.Header;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Classes into XML.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.23
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Skeleton {

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        App.class.getResourceAsStream("xsd/skeleton.xsd")
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
                        .add("app")
                        .attr("id", this.base)
                        .append(
                            new Joined<Directive>(
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
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Iterable<Map.Entry<String, Directives>> packages() {
        final long start = System.currentTimeMillis();
        final Collection<Map.Entry<String, Directives>> all =
            new CopyOnWriteArrayList<>();
        new UncheckedScalar<>(
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
        Logger.info(
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
                .attr("id", ctc.getSimpleName())
                .append(new XmlClass(ctc))
                .up()
        );
    }

}
