package org.jpeek.graph;

import com.jcabi.xml.XML;
import java.util.List;
import java.util.Map;

public final class NodeConnections {
    private final Map<XML, Node> byXml;
    private final Map<String, Node> byName;

    public NodeConnections(final XmlMethods xmlMethods) {
        this.byXml = xmlMethods.getByXml();
        this.byName = xmlMethods.getByName();
    }

    public void establishConnections() {
        for (final Map.Entry<XML, Node> entry : this.byXml.entrySet()) {
            final List<XML> calls = entry.getKey().nodes("ops/op[@code='call']");
            final Node caller = entry.getValue();
            for (final XML call : calls) {
                final String name = new XmlMethodCall(call).toString();
                if (this.byName.containsKey(name)) {
                    final Node callee = this.byName.get(name);
                    caller.connections().add(callee);
                    callee.connections().add(caller);
                }
            }
        }
    }
}
