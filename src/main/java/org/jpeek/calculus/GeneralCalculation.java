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
package org.jpeek.calculus;

import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public final class GeneralCalculation {

    /**
     * Default constructor.
     */
    private GeneralCalculation() { }

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
    public static void createReport(final Path target, final List<String> metrics)
        throws IOException {
        final Map<String, MetricPresentation> res = new HashMap<>();
        for (final String metric : metrics) {
            res.put(
                metric,
                MetricPresentation.initialize(
                    String.format(
                        "%s/%s.xml",
                        target.toString(),
                        metric
                    )
                )
            );
        }
        final Map<String, String[]> output = getResult(res);
        String template;
        template = IOUtils.toString(
            GeneralCalculation.class.getResourceAsStream("general_calculus.html")
        );
        final String classesout = String.format(
            "<p id=\"classes\">Classes: %s.</p>\n",
            output.keySet().size()
        );
        String tbody = "";
        for (final Map.Entry<String, String[]> entry : output.entrySet()) {
            tbody = tbody.concat(
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
        template = template.replace(
            "<p id=\"classes\"></p>",
            classesout
        );
        template = template.replace(
            "<tbody id=\"table_body\"></tbody>",
                String.format(
                    "<tbody id=\"table_body\">\n%s\n</tbody>",
                    tbody
                )
            );
        Files.write(
            Paths.get(
                String.format("%s%s", target.toString(), "/total.html")
            ),
            template.getBytes(StandardCharsets.UTF_8)
        );
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

