package org.jpeek.calculus.java.fix;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.HashMap;
import java.util.List;
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
    List<String> classes = skeleton.xpath("//class/@id");
    String finalRes = tempRes.toString().substring(0, tempRes.toString().indexOf("<class"));

    for (int iter = 0; iter < classes.size(); iter++) {
      List<XML> methods = skeleton.nodes(String
          .format("//class[@id ='%s']/methods/method[@ctor='false' and @abstract='false']",
              classes.get(iter)));

      List<XML> edges = tempRes
          .nodes(String.format("//class[@id =\"%s\"]/vars/edges/edge", classes.get(iter)));

      String classPattern = "";
      int ncc = 0;

      double nc = Double.parseDouble(tempRes.xpath(
          String.format("//class[@id ='%s']/vars/var[@id =\"nc\"]/text()", classes.get(iter)))
          .get(0));
      double nmp = Double.parseDouble(tempRes.xpath(
          String.format("//class[@id ='%s']/vars/var[@id =\"nmp\"]/text()", classes.get(iter)))
          .get(0));

      if (methods.size() > 1) {

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

        ncc = f.connectedComponents();
        double value = nc / (nmp * ncc);

        classPattern = String.format(" <class id=\"%s\" value=\"%s\">\n", classes.get(iter), value);
      } else {
        classPattern = String.format(" <class id=\"%s\" value=\"NaN\">\n", classes.get(iter));
      }

      classPattern += "            <vars>\n" +
          String.format("               <var id=\"methods\">%s</var>\n", methods.size()) +
          String.format("               <var id=\"nc\">%s</var>\n", nc) +
          String.format("               <var id=\"ncc\">%s</var>\n", ncc) +
          String.format("               <var id=\"nmp\">%s</var>\n", nmp);

      classPattern += "            </vars>\n         </class>\n";
      finalRes += classPattern;
    }

    finalRes += "      </package>";
    finalRes += tempRes.toString().substring(tempRes.toString().indexOf("</app"));
    return new XMLDocument(finalRes);
  }

  private String clearSpaces(String old) {
    return old.replace(" ", "").replace("\n", "");
  }
}
