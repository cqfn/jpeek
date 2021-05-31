package org.jpeek;

import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class GeneralCalculation {
    public static void createReport(Path target, List<String> metrics) throws IOException {
        Map<String, MetricPresentation> res = new HashMap<>();
        for(String metric : metrics){
            res.put(metric,new MetricPresentation(target.toString()+ String.format("/%s.html",metric)));
        }
        HashMap<String, String[]> output = getResult(res);
        String template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n" +
                "   <head>\n" +
                "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "      <meta charset=\"UTF-8\" />\n" +
                "      <meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\" />\n" +
                "      <meta content=\"jpeek metrics\" name=\"description\" />\n" +
                "      <meta content=\"code quality metrics\" name=\"keywords\" />\n" +
                "      <meta content=\"jpeek.org\" name=\"author\" />\n" +
                "      <link href=\"https://www.jpeek.org/logo.png\" rel=\"shortcut icon\" />\n" +
                "      <link href=\"https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css.min.css\" rel=\"stylesheet\" />\n" +
                "      <link href=\"jpeek.css\" rel=\"stylesheet\" /><script src=\"https://cdnjs.cloudflare.com/ajax/libs/sortable/0.8.0/js/sortable.min.js\" type=\"text/javascript\"> </script><title>TOTAL</title>\n" +
                "   </head>\n" +
                "   <body>\n" +
                "      <p><a href=\"https://i.jpeek.org\"><img alt=\"logo\" src=\"https://www.jpeek.org/logo.svg\" style=\"height:60px\" /></a></p>\n" +
                "      <p><a href=\"index.html\">Back to index</a></p>\n" +
                "      <h1>TOTAL</h1>\n" +
                "      <p>Min: 0, max: 1, yellow zone: <code>[0.2000 .. 0.4000]</code>.\n" +
                "      </p>\n" +
                String.format("      <p>Classes: %s.</p>\n", output.keySet().size()) +
                "      <table data-sortable=\"true\">\n" +
                "         <colgroup>\n" +
                "            <col />\n" +
                "            <col />\n" +
                "         </colgroup>\n" +
                "         <thead>\n" +
                "            <tr>\n" +
                "               <th>Class</th>\n" +
                "               <th style=\"text-align:right\">Main Result</th>\n" +
                "\t       <th style=\"text-align:right\">Type Result</th>\n" +
                "\t       <th style=\"text-align:right\">Refactor Result</th>\n" +
                "            </tr>\n" +
                "         </thead>\n" +
                "         <tbody>\n";
        for (Map.Entry<String, String[]> entry : output.entrySet()) {
            template += "<tr>\n" +
                    String.format("               <td><code title=\"%s\">%s</code></td>\n", entry.getKey(), entry.getKey()) +
                    String.format("               <td style=\"text-align:right;\">%s</td>\n", entry.getValue()[0]) +
                    String.format("               <td style=\"text-align:right\">%s</td>\n", entry.getValue()[1]) +
                    String.format("               <td style=\"text-align:right\">%s</td>\n", entry.getValue()[2])+
                    "            </tr>";
        }
        template += "\n" +
                "         </tbody>\n" +
                "      </table>\n" +
                "      <footer style=\"color:gray;font-size:75%;\">\n" +
                "         <p>This report was generated by <a href=\"https://www.jpeek.org\">jpeek 1.0-SNAPSHOT</a>\n" +
                "         </p>\n" +
                "      </footer>\n" +
                "   </body>\n" +
                "</html>";
        try{
            File file = new File(target.toString() + "/total.html");

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(template);
            bw.close();

            System.out.println("Done");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    static HashMap<String,String[]> getResult(Map<String, MetricPresentation> res){
        List<String> classNames = new ArrayList<>(new ArrayList<MetricPresentation>(res.values()).get(0).values.keySet());
        for(int i = 0; i < classNames.size(); i++){
            classNames.set(i, classNames.get(i).substring(classNames.get(i).lastIndexOf('.')+1));
        }

        HashMap<String,String[]> output = new HashMap<>();
        for(String elem : classNames){
            HashMap<String, Double> values = new HashMap<String, Double>();
            for(String metric : res.keySet()){
                MetricPresentation value = res.get(metric);
                String elemValue = value.values.get(elem);
                if(elemValue == "NaN" || elemValue == null){
                    values.put(metric, -1d);
                }
                else{
                    values.put(metric, Double.parseDouble(elemValue));
                }
            }
            output.put(elem, calculateRes(values.get("CCM"),values.get("LCC"),values.get("NHD"),
                    values.get("MMAC"),values.get("TCC"),values.get("PCC"), values.get("LCOM2"),
                    values.get("LCOM5")));

        }
        return output;
    }

    static String[] calculateRes(double ccmRes, double lccRes, double nhdRes, double mmacRes,
                          double tccRes, double pccRes, double lcom2Res, double lcom5Res){
        double indirectRes = (ccmRes + lccRes)/2;
        double typeRes = (nhdRes + mmacRes)/2;
        double refactorRes = ((1-lcom2Res) + (1 -lcom5Res) + tccRes + pccRes)/4;
        return new String[]{String.valueOf(indirectRes), String.valueOf(typeRes), String.valueOf(refactorRes)};

//        if(Double.isNaN(lccRes)){
//            indirectRes = ccmRes/2;
//        }
//        else if(Double.isNaN(ccmRes)){
//            indirectRes = lccRes/2;
//        }
//        if(indirectRes >= 0.7)
//            return new String[]{String.valueOf(indirectRes), "-", "-"};
//        double typeRes = (nhdRes + mmacRes)/2;
//        if(typeRes >= 0.5){
//            return new String[]{"-", String.valueOf(typeRes), "-"};
//        }
//        if(lcom5Res == -1 || Double.isNaN(lcom5Res) || Double.isNaN(pccRes)){
//            return new String[]{String.valueOf(indirectRes), "-", "-"};
//        }
//        double refactorRes = ((1-lcom2Res) + (1 -lcom5Res) + tccRes + pccRes)/4;
//        double oopRes = (lccRes +ccmRes +mmacRes +nhdRes +lcom5Res +pccRes +tccRes +(1 -lcom2Res))/8;
//        if(oopRes >= 0.5){
//            return new String[]{String.valueOf(oopRes), "-", String.valueOf(refactorRes)};
//        }
//        else{
//            return new String[]{String.valueOf(indirectRes), "-", String.valueOf(refactorRes)};
//        }
    }
}

class MetricPresentation{
    String path;
    Document doc;
    Map<String, String> values = new HashMap<String, String>();
    public MetricPresentation(String path) throws IOException {
        this.path = path;
        File input = new File(path);
        doc = Jsoup.parse(input, "UTF-8");
        parseValues();
    }
    void parseValues(){
        Element table = doc.select("table").get(0);
        String name = table.select("code").get(0).text();
        name = name.substring(name.lastIndexOf('.') + 1);
        Elements rows = table.select("tr");
        for(int i = 1; i < rows.size(); i++){
            String label = rows.get(i).select("td").get(0).select("code").get(0).text();
            label = label.substring(label.lastIndexOf('.') + 1);
            String value = rows.get(i).select("td").get(1).text();
            values.put(label,value);
        }
    }
}
