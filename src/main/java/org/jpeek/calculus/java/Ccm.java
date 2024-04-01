/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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
package org.jpeek.calculus.java;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.text.FormattedText;
import org.jpeek.calculus.Calculus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * CCM metric Java calculus.
 * This class implements the Calculus interface to provide functionality
 * for computing the CCM metric for Java code.
 * @since 0.30.25
 */
public final class Ccm implements Calculus {

    @Override
    public XML node(
        final String metric,
        final Map<String, Object> params,
        final XML skeleton
    ) {
        if (!"ccm".equalsIgnoreCase(metric)) {
            throw new IllegalArgumentException(
                new FormattedText(
                    "This metric is CCM, not %s.", metric
                ).toString()
            );
        }
        final XSLDocument doc = new XSLDocument(
            new UncheckedInput(
                new ResourceOf("org/jpeek/metrics/CCM.xsl")
            ).stream()
        );
        final XML meta = addMetaInformation(skeleton, params);
        return doc.transform(meta);
    }

    /**
     * Adds meta information to the skeleton XML document.
     * This method modifies the skeleton XML document by adding meta information
     * about the computed CCM metric.
     * @param skeleton The skeleton XML document representing the code structure.
     * @param params Parameters for the computation.
     * @return The modified XML document containing meta information.
     */
    private static XML addMetaInformation(final XML skeleton, final Map<String, Object> params) {
        try {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument();
            final Element meta = doc.createElement("meta");
            final List<XML> packages = skeleton.nodes("//package");
            for (final XML pack : packages) {
                final Element tag = doc.createElement("package");
                tag.setAttribute(
                    "id", pack.node().getAttributes().getNamedItem("id").getNodeValue()
                );
                final List<XML> classes = pack.nodes("class");
                for (final XML clazz: classes) {
                    final Element sub = doc.createElement("class");
                    sub.appendChild(addNccTag(doc, clazz, params));
                    sub.setAttribute(
                        "id",
                        clazz.node().getAttributes().getNamedItem("id").getNodeValue()
                    );
                    tag.appendChild(sub);
                }
                meta.appendChild(tag);
            }
            final Node repr = skeleton.node();
            final Node text = repr.getFirstChild().getOwnerDocument()
                .importNode(doc.createTextNode("\n"), true);
            final Node node = repr.getFirstChild().getOwnerDocument()
                .importNode(meta, true);
            repr.getFirstChild().appendChild(text);
            repr.getFirstChild().appendChild(node);
            return new XMLDocument(repr);
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Adds NCC (Number of Component Connections) tag to the XML document.
     * This method calculates the NCC for a given class and adds it as a tag to the XML document.
     * @param doc The XML document to which the NCC tag will be added.
     * @param clazz The XML representation of the class.
     * @param params Parameters for the computation (unused).
     * @return The NCC node.
     */
    private static Node addNccTag(final Document doc, final XML clazz,
        final Map<String, Object> params
    ) {
        final Element ncc = doc.createElement("ncc");
        ncc.appendChild(doc.createTextNode(calculateComponents(clazz, params).toString()));
        return ncc;
    }

    /**
     * Calculates the number of components for a given class.
     * This method calculates the number of components for a class using the Union-Find algorithm.
     * @param clazz The XML representation of the class.
     * @param params Parameters for the computation.
     * @return The number of components.
     */
    private static Integer calculateComponents(final XML clazz, final Map<String, Object> params) {
        final Map<String, List<String>> connections = new HashMap<>();
        final Map<String, String> parents = new HashMap<>();
        for (final XML method : clazz.nodes("methods/method")) {
            if (!params.containsKey("include-static-methods")
                && method.node().getAttributes().getNamedItem("static").getNodeValue()
                    .equals("true")) {
                continue;
            }
            final String name = method.node().getAttributes().getNamedItem("name").getNodeValue();
            if (!params.containsKey("include-ctors") && name.equals("<init>")) {
                continue;
            }
            parents.put(name, name);
            final List<XML> ops = method.nodes("ops/op");
            for (final XML operation : ops) {
                final String var = operation.node().getTextContent();
                if (connections.containsKey(var)) {
                    connections.get(var).add(name);
                } else {
                    final List<String> init = new ArrayList<>(0);
                    init.add(name);
                    connections.put(var, init);
                }
            }
        }
        return unionFind(parents, connections);
    }

    /**
     * Performs the Union-Find algorithm to calculate the number of components.
     * This method implements the Union-Find algorithm to calculate the number of components.
     * @param parents The map representing the parent relationship.
     * @param connections The map representing the connections between variables and methods.
     * @return The number of components.
     */
    private static Integer unionFind(final Map<String, String> parents,
        final Map<String, List<String>> connections
    ) {
        int answer = parents.size();
        for (final List<String> conns : connections.values()) {
            final String initial = conns.get(0);
            for (final String connectable : conns) {
                if (!parents.get(initial).equals(parents.get(connectable))) {
                    answer -= 1;
                }
                unite(initial, connectable, parents);
            }
        }
        return answer;
    }

    /**
     * Gets the parent of a node using the Union-Find algorithm.
     * This method retrieves the parent of a node using the Union-Find algorithm.
     * @param node The node whose parent is to be found.
     * @param parents The map representing the parent relationship.
     * @return The parent of the node.
     */
    private static String getParent(final String node, final Map<String, String> parents) {
        String ancestor = node;
        while (!parents.get(ancestor).equals(ancestor)) {
            ancestor = parents.get(ancestor);
        }
        return ancestor;
    }

    /**
     * Unites two nodes using the Union-Find algorithm.
     * This method unites two nodes using the Union-Find algorithm.
     * @param node The first node.
     * @param son The second node.
     * @param parents The map representing the parent relationship.
     */
    private static void unite(final String node, final String son,
        final Map<String, String> parents
    ) {
        final String root = getParent(node, parents);
        final String attachable = getParent(son, parents);
        if (!root.equals(attachable)) {
            parents.put(attachable, root);
        }
    }
}
