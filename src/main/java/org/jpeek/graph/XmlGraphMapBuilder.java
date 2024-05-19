package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.util.Map;
import org.cactoos.map.MapOf;
import org.cactoos.text.FormattedText;
import org.jpeek.skeleton.Skeleton;

public final class XmlGraphMapBuilder {

    private final Skeleton skeleton;
    private final String pname;
    private final String cname;

    public XmlGraphMapBuilder(final Skeleton skeleton, final String pname, final String cname) {
        this.skeleton = skeleton;
        this.pname = pname;
        this.cname = cname;
    }

    public Map<XML, Node> buildByXmlMap() {
        return new MapOf<>(
                method -> method,
                method -> new Node.Simple(
                        new XmlMethodSignature(
                                this.skeleton.xml()
                                        .nodes(
                                                new FormattedText(
                                                        "//package[@id='%s']", this.pname
                                                ).toString()
                                        ).get(0)
                                        .nodes(
                                                new FormattedText(
                                                        "//class[@id='%s']", this.cname
                                                ).toString()
                                        ).get(0),
                                method
                        ).asString()
                ),
                this.skeleton.xml().nodes(
                        "//methods/method[@ctor='false' and @abstract='false']"
                )
        );
    }

    public Map<String, Node> buildByNameMap(final Map<XML, Node> byxml) {
        return new MapOf<>(
                Node::name,
                node -> node,
                byxml.values()
        );
    }
}
