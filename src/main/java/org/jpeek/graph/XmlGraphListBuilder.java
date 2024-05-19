package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;

public final class XmlGraphListBuilder {

    private final Map<XML, Node> byxml;
    private final Map<String, Node> byname;

    public XmlGraphListBuilder(final Map<XML, Node> byxml, final Map<String, Node> byname) {
        this.byxml = byxml;
        this.byname = byname;
    }

    public List<Node> buildNodesList() {
        for (final Map.Entry<XML, Node> entry : this.byxml.entrySet()) {
            final List<XML> calls = entry.getKey().nodes("ops/op[@code='call']");
            final Node caller = entry.getValue();
            for (final XML call : calls) {
                final String name = new XmlMethodCall(call).toString();
                if (this.byname.containsKey(name)) {
                    final Node callee = this.byname.get(name);
                    caller.connections().add(callee);
                    callee.connections().add(caller);
                }
            }
        }
        return new ListOf<>(this.byxml.values());
    }
}
