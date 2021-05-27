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
      String name = methods.get(i).node().getAttributes().getNamedItem("name").getNodeValue();
      String desc = methods.get(i).node().getAttributes().getNamedItem("desc").getNodeValue();

      methodIds.put(name + desc, i);
    }

    final Pattern pattern = Pattern.compile(
        "<method>[\\n\\r\\s]+<name>(\\w+)<\\/name>[\\n\\r\\s]+(<desc>([\\n\\r\\s\\w;\\/()]+)<\\/desc>|<desc\\/>)[\\n\\r\\s]+<\\/method>"
    );

    FindConnectedComponents f = new FindConnectedComponents(methods.size());

    for (XML method : edges) {
      Matcher name = pattern.matcher(method.toString());

      int from = -1;
      int to = -1;

      if (name.find()) {
        if (name.group(2).equals("<desc/>")) {
          from = methodIds.get(clearSpaces(name.group(1)));
        } else {
          from = methodIds.get(clearSpaces(name.group(1) + name.group(3)));
        }
      }

      if (name.find()) {
        if (name.group(2).equals("<desc/>")) {
          to = methodIds.get(clearSpaces(name.group(1)));
        } else {
          to = methodIds.get(clearSpaces(name.group(1) + name.group(3)));
        }
      }

      if (from != -1 && to != -1) {
        f.addEdge(from, to);
      }
    }

    int ncc = f.connectedComponents();

    double nc = Double.parseDouble(tempRes.xpath("//var[@id=\"nc\"]/text()").get(0));
    double nmp = Double.parseDouble(tempRes.xpath("//var[@id=\"nmp\"]/text()").get(0));
    double value = nc / (nmp * ncc);

    final Pattern nccReplacePattern = Pattern.compile("ncc\\\">(\\w+)");
    final Pattern valueReplacePattern = Pattern.compile("value=\\\"([0-9]+.[0-9]+)");

    String finalRes = tempRes.toString();
    String finalValue = String.format(Locale.US, "value=\"%.2f\"", value);

    if (nccReplacePattern.matcher(removeTempVars()).find()) {
      finalRes = nccReplacePattern.matcher(removeTempVars())
          .replaceAll(String.format("ncc\">%d", ncc));
    }

    if (valueReplacePattern.matcher(finalRes).find()) {
      finalRes = valueReplacePattern.matcher(finalRes).replaceAll(finalValue);
    } else {
      finalRes = finalRes.replace("value=\"NaN\"", finalValue);
    }

    return new XMLDocument(finalRes);
  }

  private String removeTempVars() {
    final Pattern finalReplacePattern = Pattern
        .compile("(   )(<var id=\\\"edges\\\">)(?s).*(</vars>)");

    String finalRes = finalReplacePattern.matcher(tempRes.toString()).replaceAll("</vars>");
    return finalRes.replaceAll("               <var id=\"edges\"/>\n", "");
  }

  private String clearSpaces(String old) {
    return old.replace(" ", "").replace("\n", "");
  }
}
