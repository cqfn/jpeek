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
package org.jpeek.calculus.xsl;

import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.jpeek.calculus.Calculus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Metrics ccm xsl calculus. Use an xsl sheet to transform the input skeleton into
 * the xml containing the calculation.
 *
 * @since 0.30.9
 */
public final class CcmXslCalculus implements Calculus {

    @Override
    public XML node(final String metric, final Map<String, Object> params,
        final XML skeleton) throws IOException {
        final XSLDocument doc = new XSLDocument(
            new UncheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("org/jpeek/metrics/%s.xsl", metric)
                    )
                )
            ).asString(),
            Sources.DUMMY,
            params
        );
        final XML meta = addMetaTag(skeleton);
        return doc.transform(meta);
    }

    /**
     * Adds a meta tag to the skeleton XML.
     * @param skeleton The skeleton XML to which the meta tag will be added
     * @return The modified XML with the meta tag added
     */
    private static XML addMetaTag(final XML skeleton) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        final Document doc;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            final Element meta = doc.createElement("meta");
            Node packages = skeleton.node().getFirstChild().getFirstChild().getNextSibling()
                .getFirstChild();
            while (Objects.nonNull(packages)) {
                if (packages.getTextContent().matches("\\s*")) {
                    packages = packages.getNextSibling();
                    continue;
                }
                final Node child = createPackageTag(doc, packages);
                meta.appendChild(child);
                packages = packages.getNextSibling();
            }
            doc.appendChild(meta);
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        final Node repr = skeleton.node().getFirstChild();
        final Node node = repr.getOwnerDocument()
            .importNode(doc.getDocumentElement(), true);
        repr.appendChild(node);
        return new XMLDocument(repr);
    }

    /**
     * Creates a package tag element in the XML document.
     * @param doc The XML document to which the package tag will be added
     * @param packages The node representing the package
     * @return The package tag element created in the XML document
     */
    private static Node createPackageTag(final Document doc, final Node packages) {
        final NamedNodeMap map = packages.getAttributes();
        final Element root = doc.createElement("package");
        final Node id = map.getNamedItem("id");
        root.setAttribute("id", id.getNodeValue());
        Node classes = packages.getFirstChild();
        while (Objects.nonNull(classes)) {
            if (classes.getTextContent().matches("\\s*")) {
                classes = classes.getNextSibling();
                continue;
            }
            final Node child = createClassTag(doc, classes);
            root.appendChild(child);
            classes = classes.getNextSibling();
        }
        return root;
    }

    /**
     * Creates a class tag element in the XML document.
     *
     * This method constructs a class tag element in the XML document based on the
     * information provided by the given node representing a class. It traverses
     * through the attributes of the class node to extract necessary information
     * and constructs the class tag element accordingly.
     *
     * Additionally, this method adds a child element representing a metric called
     * "ncc" to the class tag.
     *
     * @param doc The XML document to which the class tag will be added
     * @param classes The node representing the class
     * @return The class tag element created in the XML document
     */
    private static  Node createClassTag(final Document doc, final Node classes) {
        final NamedNodeMap map = classes.getAttributes();
        final Element root = doc.createElement("class");
        final Node id = map.getNamedItem("id");
        root.setAttribute("id", id.getNodeValue());
        final Integer value = 1;
        final Element ncc = doc.createElement("ncc");
        ncc.setTextContent(value.toString());
        root.appendChild(ncc);
        return root;
    }
}
