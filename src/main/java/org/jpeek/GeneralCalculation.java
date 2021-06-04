/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jpeek;

import com.jcabi.log.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates aggregated value for cohesion metrics.
 *
 * @since 1.0.0
 * @checkstyle StringLiteralsConcatenationCheck (250 lines)
 */
public class GeneralCalculation {

    /**
     * Created report with aggregated value.
     *
     * @param target Path to report file
     * @param metrics List of metrics used in calculation
     * @throws IOException If fails
     */
    @SuppressWarnings({
        "PMD.AvoidDuplicateLiterals",
        "PMD.ExcessiveMethodLength"
    })
    public void createReport(final Path target, final List<String> metrics) throws IOException {
        final Map<String, MetricPresentation> res = new HashMap<>();
        for (final String metric : metrics) {
            res.put(
                metric,
                new MetricPresentation(
                    String.format(
                        "%s/%s.html",
                        target.toString(),
                        metric
                    )
                )
            );
        }
        final Map<String, String[]> output = getResult(res);
        String template = String.format(
            "%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><html",
            "xmlns=\"http://www.w3.org/1999/xhtml \" lang=\"en\">\n",
            "   <head>\n",
            "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n",
            "      <meta charset=\"UTF-8\" />\n",
            "      <meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\" />\n",
            "      <meta content=\"jpeek metrics\" name=\"description\" />\n",
            "      <meta content=\"code quality metrics\" name=\"keywords\" />\n",
            "      <meta content=\"jpeek.org\" name=\"author\" />\n",
            "      <link href=\"https://www.jpeek.org/logo.png\" rel=\"shortcut icon\" />\n",
            "      <link href=",
            "https://cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css.min.css",
            "rel=\"stylesheet\" />\n",
            "      <link href=\"jpeek.css\" rel=\"stylesheet\" /><script ",
            "src=\"https://cdnjs.cloudflare.com/ajax/libs/sortable/0.8.0/js/sortable.min.js\" ",
            "type=\"text/javascript\"> </script><title>TOTAL</title>\n",
            "   </head>\n",
            "   <body>\n",
            "      <p><a href=\"https://i.jpeek.org\"><img alt=\"logo\" ",
            "src=\"https://www.jpeek.org/logo.svg\" style=\"height:60px\" /></a></p>\n",
            "      <p><a href=\"index.html\">Back to index</a></p>\n",
            "      <h1>TOTAL</h1>\n",
            "      <p>Min: 0, max: 1, yellow zone: <code>[0.2000 .. 0.4000]</code>.\n",
            "      </p>\n",
            String.format(
                "      <p>Classes: %s.</p>\n",
                output.keySet().size()
            ),
            "      <table data-sortable=\"true\">\n",
            "         <colgroup>\n",
            "            <col />\n",
            "            <col />\n",
            "         </colgroup>\n",
            "         <thead>\n",
            "            <tr>\n",
            "               <th>Class</th>\n",
            "               <th style=\"text-align:right\">Main Result</th>\n",
            "\t       <th style=\"text-align:right\">Type Result</th>\n",
            "\t       <th style=\"text-align:right\">Refactor Result</th>\n",
            "            </tr>\n",
            "         </thead>\n",
            "         <tbody>\n"
        );
        for (final Map.Entry<String, String[]> entry : output.entrySet()) {
            template = template.concat(
                String.format(
                    "%s%s%s%s%s%s", "<tr>\n",
                    String.format(
                        "               <td><code title=\"%s\">%s</code></td>\n",
                        entry.getKey(),
                        entry.getKey()
                    ),
                    String.format(
                        "               <td style=\"text-align:right;\">%s</td>\n",
                        entry.getValue()[0]
                    ),
                    String.format(
                        "               <td style=\"text-align:right\">%s</td>\n",
                        entry.getValue()[1]
                    ),
                    String.format(
                        "               <td style=\"text-align:right\">%s</td>\n",
                        entry.getValue()[2]
                    ),
                    "            </tr>"
                )
            );
        }
        template = template.concat(
            String.format(
                "%s%s%s%s%s%s%s%s%s%s", "\n",
                "         </tbody>\n",
                "      </table>\n",
                "      <footer style=\"color:gray;font-size:75%;\">\n",
                "         <p>This report was generated by <a href=\"https://www.jpeek.org\">",
                "jpeek 1.0-SNAPSHOT</a>\n",
                "         </p>\n",
                "      </footer>\n",
                "   </body>\n",
                "</html>"
            )
        );
        try {
            final File file = new File(String.format("%s%s", target.toString(), "/total.html"));
            final BufferedWriter bwriter = Files.newBufferedWriter(file.getAbsoluteFile().toPath());
            bwriter.write(template);
            bwriter.close();
        } catch (final IOException ioex) {
            Logger.error(this, ioex.getMessage());
        }
    }

    /**
     * Gets metric calculation result.
     *
     * @param res List of metrics with path
     * @return List of metrics value
     */
    static Map<String, String[]> getResult(final Map<String, MetricPresentation> res) {
        final List<String> classnames = new ArrayList<>(
            new ArrayList<>(res.values()).get(0).getValues().keySet()
        );
        for (int ind = 0; ind < classnames.size(); ind += 1) {
            classnames.set(
                ind,
                classnames.get(ind).substring(
                    classnames.get(ind).lastIndexOf('.') + 1
                )
            );
        }
        final Map<String, String[]> output = new HashMap<>();
        for (final String elem : classnames) {
            final Map<String, Double> values = new HashMap<>();
            for (final String metric : res.keySet()) {
                final MetricPresentation value = res.get(metric);
                final String elemvalue = value.getValues().get(elem);
                if (elemvalue == null || elemvalue.equals("NaN")) {
                    values.put(metric, -1d);
                } else {
                    values.put(metric, Double.parseDouble(elemvalue));
                }
            }
            output.put(
                elem,
                calculateRes(values)
            );
        }
        return output;
    }

    /**
     * Calculates aggregated value.
     *
     * @param values Map of metrics and its values
     * @return Returns list of possible values
     */
    static String[] calculateRes(final Map<String, Double> values) {
        final double indirect = (values.get("CCM") + values.get("LCC")) / 2;
        final double type = (values.get("NHD") + values.get("MMAC")) / 2;
        final double refactor = ((1 - values.get("LCOM2")) + (1 - values.get("LCOM5"))
            + values.get("TCC") + values.get("PCC")) / 4;
        return new String[]{
            String.valueOf(indirect), String.valueOf(type), String.valueOf(refactor),
        };
    }
}
