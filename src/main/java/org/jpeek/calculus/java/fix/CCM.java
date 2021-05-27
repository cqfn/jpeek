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

  public XML getFixedResult(XML skeleton, XML tempRes) {
    List<String> classes = skeleton.xpath("//class/@id");
    String finalRes = tempRes.toString().substring(0,tempRes.toString().indexOf("<class"));
    for(int iter = 0; iter < classes.size(); iter++) {
      List<XML> methods = skeleton.nodes(String.format("//class[@id ='%s']/methods/method[@ctor='false' and @abstract='false']", classes.get(iter)));
      System.out.println("methods num: " + methods.size());
      List<XML> edges = tempRes.nodes(String.format("//class[@id ='%s']/edges/edge", classes.get(iter)));

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

      double nc = Double.parseDouble(tempRes.xpath(String.format("//class[@id ='%s']/vars/var[@id =\"nc\"]/text()", classes.get(iter))).get(0));
      double nmp = Double.parseDouble(tempRes.xpath(String.format("//class[@id ='%s']/vars/var[@id =\"nmp\"]/text()", classes.get(iter))).get(0));
      double value = nc / (nmp * ncc);

      String classPattern = String.format(" <class id=\"%s\" value=\"%s\">\n", classes.get(iter), value) +
              "            <vars>\n" +
              String.format("               <var id=\"methods\">%s</var>\n", methods.size()) +
              String.format("               <var id=\"nc\">%s</var>\n", nc) +
              String.format("               <var id=\"ncc\">%s</var>\n", ncc) +
              String.format("               <var id=\"nmp\">%s</var>\n", nmp);
      classPattern +=
      "            </vars>\n" +
      "         </class>\n";
//      final Pattern nccReplacePattern = Pattern.compile("ncc\\\">(\\w+)");
//      final Pattern valueReplacePattern = Pattern.compile("value=\\\"([0-9]+.[0-9]+)");
//      final Pattern finalReplacePattern = Pattern
//              .compile("(   )(<var id=\\\"edges\\\">)(?s).*(</vars>)");
//
//      String tempWithNccRes = nccReplacePattern.matcher(tempRes.toString())
//              .replaceAll(String.format("ncc\">%d", ncc));
//      String tempWithValueRes = valueReplacePattern.matcher(tempWithNccRes)
//              .replaceAll(String.format(Locale.US, "value=\"%.2f", value));
//      finalRes += finalReplacePattern.matcher(tempWithValueRes).replaceAll("</vars>");
      finalRes += classPattern;
    }
    finalRes += "      </package>";
    finalRes += tempRes.toString().substring(tempRes.toString().indexOf("</app"));
    return new XMLDocument(finalRes);
  }
}
