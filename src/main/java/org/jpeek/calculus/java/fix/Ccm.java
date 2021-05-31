package org.jpeek.calculus.java.fix;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jpeek.javagraph.FindConnectedComponents;

/**
 * Application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 * @checkstyle NPathComplexityCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 * @checkstyle CyclomaticComplexityCheck (500 lines)
 * @checkstyle MethodLengthCheck (500 lines)
 * @checkstyle JavaNCSSCheck (500 lines)
 * @since 0.1
 */
public class Ccm {

  private final XML skeleton;
  private final XML tempres;

  public Ccm(final XML skeleton, final XML tempres) {
    this.skeleton = skeleton;
    this.tempres = tempres;
  }

  public XML getFixedResult() {
    final List<String> packages = this.skeleton.xpath("//package/@id");
    String finalres = this.tempres.toString().substring(0, this.tempres.toString().indexOf("<package") - 6);
    for (int p = 0; p < packages.size(); p=+1) {
      finalres += String.format("      <package id=\"%s\">\n", packages.get(p));
      final List<String> classes = this.skeleton
          .xpath(String.format("//package[@id ='%s']/class/@id", packages.get(p)));
      for (int iter = 0; iter < classes.size(); iter=+1) {
        final List<XML> methods = this.skeleton.nodes(String
            .format("//class[@id ='%s']/methods/method[@ctor='false' and @abstract='false']",
                classes.get(iter)));
        final List<XML> edges = this.tempres
            .nodes(String.format("//class[@id =\"%s\"]/vars/edges/edge", classes.get(iter)));
        String classpattern = "";
        int ncc = 0;
        double nco = Double.parseDouble(this.tempres.xpath(
            String.format("//class[@id ='%s']/vars/var[@id =\"nc\"]/text()", classes.get(iter)))
            .get(0));
        double nmp = Double.parseDouble(this.tempres.xpath(
            String.format("//class[@id ='%s']/vars/var[@id =\"nmp\"]/text()", classes.get(iter)))
            .get(0));
        if (methods.size() > 1) {
          Map<String, Integer> methodIds = new HashMap<>();
          for (int i = 0; i < methods.size(); i=+1) {
            String name = methods.get(i).node()
                .getAttributes().getNamedItem("name").getNodeValue();
            String desc = methods.get(i).node()
                .getAttributes().getNamedItem("desc").getNodeValue();
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
          double value = nco / (nmp * ncc);
          classpattern = String
              .format("         <class id=\"%s\" value=\"%s\">\n", classes.get(iter), value);
        } else {
          classpattern = String
              .format("         <class id=\"%s\" value=\"NaN\">\n", classes.get(iter));
        }
        classpattern += "            <vars>\n" +
            String.format("               <var id=\"methods\">%s</var>\n", methods.size()) +
            String.format("               <var id=\"nc\">%s</var>\n", nco) +
            String.format("               <var id=\"ncc\">%s</var>\n", ncc) +
            String.format("               <var id=\"nmp\">%s</var>\n", nmp);
        classpattern += "            </vars>\n         </class>\n";
        finalres += classpattern;
      }
      finalres += "      </package>\n";
    }
    finalres += this.tempres.toString().substring(tempres.toString().indexOf("</app"));
    return new XMLDocument(finalres);
  }

  private String clearSpaces(String old) {
    return old.replace(" ", "").replace("\n", "");
  }
}
