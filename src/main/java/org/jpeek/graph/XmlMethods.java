package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapOf;
import org.jpeek.skeleton.Skeleton;

public final class XmlMethods {
    private final Map<XML, Node> byXml;
    private final Map<String, Node> byName;

    public XmlMethods(final Skeleton skeleton, final String pname, final String cname) {
        this.byXml = new MapOf<>(
            method -> method,
            method -> new Node.Simple(
                new XmlMethodSignature(
                    skeleton.xml()
                        .nodes(String.format("//package[@id='%s']", pname))
                        .get(0)
                        .nodes(String.format("//class[@id='%s']", cname))
                        .get(0),
                    method
                ).asString()
            ),
            skeleton.xml().nodes("//methods/method[@ctor='false' and @abstract='false']")
        );
        this.byName = new MapOf<>(Node::name, node -> node, this.byXml.values());
    }

    public Map<XML, Node> getByXml() {
        return this.byXml;
    }

    public Map<String, Node> getByName() {
        return this.byName;
    }
}
