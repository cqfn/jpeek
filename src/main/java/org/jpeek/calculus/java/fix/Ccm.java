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
package org.jpeek.calculus.java.fix;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jpeek.graph.java.FindConnectedComponents;

/**
 * Application.
 * <p>There is no thread-safety guarantee.
 *
 * @since 1.0.0
 * @checkstyle NestedIfDepthCheck (300 lines)
 * @checkstyle MagicNumberCheck (300 lines)
 * @checkstyle ExecutableStatementCountCheck (300 lines)
 */
public final class Ccm {

    /**
     * XML Skeleton.
     */
    private final XML skeleton;

    /**
     * The transformed XML skeleton.
     */
    private final XML tempres;

    /**
     * Initializes object of class for fixing NCC value of CCM metric.
     *
     * @param skeleton XML Skeleton
     * @param tempres The Transformed XML skeleton.
     */
    public Ccm(final XML skeleton, final XML tempres) {
        this.skeleton = skeleton;
        this.tempres = tempres;
    }

    /**
     * Updates the transformed xml with proper NCC value.
     *
     * @return XML with fixed NCC.
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public XML getFixedResult() {
        final List<String> packages = this.skeleton.xpath("//package/@id");
        String finalres = "";
        if (this.tempres.toString().indexOf("<package") > 6) {
            finalres = this.tempres.toString().substring(
                0,
                this.tempres.toString().indexOf("<package") - 6
            );
        }
        for (final String apackage : packages) {
            finalres = finalres.concat(
                String.format(
                    "      <package id=\"%s\">\n",
                    apackage
                )
            );
            final List<String> classes = this.skeleton.xpath(
                String.format(
                    "//package[@id ='%s']/class/@id",
                    apackage
                )
            );
            for (final String aclass : classes) {
                finalres = this.calc(aclass, finalres);
            }
            finalres = finalres.concat("      </package>\n");
        }
        if (this.tempres.toString().indexOf("</app") > 1) {
            finalres = finalres.concat(
                this.tempres.toString().substring(
                    this.tempres.toString().indexOf("</app")
                )
            );
        }
        return new XMLDocument(finalres);
    }

    /**
     * Calculates class value.
     * @param aclass XML class as String
     * @param finalres Final result of whole project
     * @return Final result with class value
     */
    @SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.CyclomaticComplexity",
        "PMD.NPathComplexity"
    })
    private String calc(final String aclass, final String finalres) {
        final List<XML> methods = this.skeleton.nodes(
            String.format(
                "//class[@id ='%s']/methods/method[@ctor='false' and @abstract='false']",
                aclass
            )
        );
        final List<XML> edges = this.tempres.nodes(
            String.format(
                "//class[@id =\"%s\"]/vars/edges/edge",
                aclass
            )
        );
        String classpattern;
        int ncc = 0;
        final double nco = Double.parseDouble(
            this.tempres.xpath(
                String.format(
                    "//class[@id ='%s']/vars/var[@id =\"nc\"]/text()",
                    aclass
                )
            ).get(0)
        );
        final double nmp = Double.parseDouble(
            this.tempres.xpath(
                String.format(
                    "//class[@id ='%s']/vars/var[@id =\"nmp\"]/text()",
                    aclass
                )
            ).get(0)
        );
        if (methods.size() > 1) {
            ncc = getNcc(
                methods,
                edges
            );
            final double value = nco / (nmp * ncc);
            classpattern = String.format(
                "         <class id=\"%s\" value=\"%s\">\n",
                aclass,
                value
            );
        } else {
            classpattern = String.format(
                "         <class id=\"%s\" value=\"NaN\">\n",
                aclass
            );
        }
        classpattern = classpattern.concat(
            String.format(
                "%s%s%s%s%s",
                "            <vars>\n",
                String.format(
                    "               <var id=\"methods\">%s</var>\n",
                    methods.size()
                ),
                String.format(
                    "               <var id=\"nc\">%s</var>\n",
                    nco
                ),
                String.format(
                    "               <var id=\"ncc\">%s</var>\n",
                    ncc
                ),
                String.format(
                    "               <var id=\"nmp\">%s</var>\n",
                    nmp
                )
            )
        );
        classpattern = classpattern.concat(
            "            </vars>\n         </class>\n"
        );
        return finalres.concat(classpattern);
    }

    /**
     * Gets class methods and calls class for searching number of connected components.
     * @param methods List of methods
     * @param edges List of connections among methods
     * @return Number of connected components
     */
    @SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.CyclomaticComplexity",
        "PMD.NPathComplexity"
    })
    private static int getNcc(final List<XML> methods, final List<XML> edges) {
        final Map<String, Integer> methodids = new HashMap<>();
        for (int methodind = 0; methodind < methods.size(); methodind += 1) {
            final String name = methods.get(methodind).node()
                .getAttributes().getNamedItem("name").getNodeValue();
            final String desc = methods.get(methodind).node()
                .getAttributes().getNamedItem("desc").getNodeValue();
            methodids.put(name + desc, methodind);
        }
        final Pattern pattern = Pattern.compile(
            String.format(
                "%s%s%s",
                "<method>[\\n\\r\\s]+<name>(\\w+)<\\/name>[\\n\\r\\s]+",
                "(<desc>([\\n\\r\\s\\w;\\/()]+)<\\/desc>",
                "|<desc\\/>)[\\n\\r\\s]+<\\/method>"
            )
        );
        final FindConnectedComponents compsearch =
            new FindConnectedComponents(
                methods.size()
            );
        compsearch.initGraph();
        for (final XML method : edges) {
            final Matcher name = pattern.matcher(method.toString());
            int from = -1;
            int eto = -1;
            if (name.find()) {
                if (name.group(2).equals("<desc/>")) {
                    from = methodids.get(
                        clearSpaces(
                            name.group(1)
                        )
                    );
                } else {
                    from = methodids.get(
                        clearSpaces(
                            String.format(
                                "%s%s",
                                name.group(1),
                                name.group(3)
                            )
                        )
                    );
                }
            }
            if (name.find()) {
                if (name.group(2).equals("<desc/>")) {
                    eto = methodids.get(
                        clearSpaces(name.group(1))
                    );
                } else {
                    eto = methodids.get(
                        clearSpaces(
                            String.format(
                                "%s%s",
                                name.group(1),
                                name.group(3)
                            )
                        )
                    );
                }
            }
            if (from != -1 && eto != -1) {
                compsearch.addEdge(from, eto);
            }
        }
        return compsearch.connectedComponents();
    }

    /**
     * Clears extra spaces in final result.
     * @param old String with extra spaces
     * @return String without extra spaces
     */
    private static String clearSpaces(final String old) {
        return old.replace(" ", "").replace("\n", "");
    }
}

