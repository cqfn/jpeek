package org.jpeek.calculus.java.fix;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jpeek.javagraph.FindConnectedComponents;

public class CCM {

  private final XML skeleton;
  private final XML tempRes;

  public CCM(XML skeleton, XML tempRes) {
    this.skeleton = skeleton;
    this.tempRes = tempRes;
  }

  public XML getFixedResult() {
    List<XML> methods = skeleton.nodes("//methods/method[@ctor='false' and @abstract='false']");
    List<XML> edges = tempRes.nodes("//edge");

    if (methods.size() <= 1) {
      return new XMLDocument(removeTempVars());
    }

    Map<String, Integer> methodIds = new HashMap<>();

    for (int i = 0; i < methods.size(); i++) {
      methodIds.put(((XMLDocument) methods.get(i)).xpath("//method/@name").get(i), i);
    }

    final Pattern pattern = Pattern
        .compile("(<method>)(\\w+)(</method>)(\\r\\n|\\r|\\n   )(<method>)(\\w+)(</method>)");

    FindConnectedComponents f = new FindConnectedComponents(methods.size());

    for (XML method : edges) {
      Matcher name = pattern.matcher(method.toString());

      if (name.find()) {
        f.addEdge(methodIds.get(name.group(2)), methodIds.get(name.group(6)));
      }
    }

    int ncc = f.connectedComponents();

    double nc = Double.parseDouble(tempRes.xpath("//var[@id=\"nc\"]/text()").get(0));
    double nmp = Double.parseDouble(tempRes.xpath("//var[@id=\"nmp\"]/text()").get(0));
    double value = nc / (nmp * ncc);

    final Pattern nccReplacePattern = Pattern.compile("ncc\\\">(\\w+)");
    final Pattern valueReplacePattern = Pattern.compile("value=\\\"([0-9]+.[0-9]+)");

    String tempWithNccRes = nccReplacePattern.matcher(removeTempVars())
        .replaceAll(String.format("ncc\">%d", ncc));
    String finalRes = valueReplacePattern.matcher(tempWithNccRes)
        .replaceAll(String.format(Locale.US, "value=\"%.2f", value));

    return new XMLDocument(finalRes);
  }

  private String removeTempVars() {
    final Pattern finalReplacePattern = Pattern
        .compile("(   )(<var id=\\\"edges\\\">)(?s).*(</vars>)");

    String finalRes = finalReplacePattern.matcher(tempRes.toString()).replaceAll("</vars>");
    return finalRes.replaceAll("               <var id=\"edges\"/>\n", "");
  }
}
