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

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;

/**
 * Metrics calculus interface.
 * @since 0.30.9
 * @todo #412:30min We start implementing LCOM4Calculus implementation. We should continue
 *  calculating real 'class@value' and 'class/vars/var[@id=pairs]/text' values and remove
 *  @Disabled annotation on all Lcom4CalculusTest class. If we choose to calculate LCOM4 with Java
 *  only we should also remove LCOM4.xsl part that is doing the calculus and let the xsl here just
 *  to build the structure of the xml result. This part is especially the one calculating
 *  "xsl:variable name='E'" L73->L89, and the one doing the division L97 -> L99
 * @todo #449:30min The `node` method in this interface was designed with only
 *  XSL implementation in mind - it uses the `metric` parameter to select the
 *  XSL file and uses that file to transform the `skeleton`. This makes Java
 *  based implementations a little awkward because the `metric` parameter
 *  becomes redundant: there is a Java implementation for each metric, and these
 *  implementations already know which metric they are for. The question becomes
 *  - how to select correct java implementation for a given metric and integrate
 *  it seamlessly with XSL calculus in `XslReport`. One option could be removing
 *  the `metric` parameter from the method and injecting a Calculus for a
 *  concrete metric in `XslReport` directly. Another could be implementing
 *  Chain Of Responsibility pattern. Decide on the best way to integrate Java
 *  based Calculus with XSL based Calculus in `XslReport` and implement it.
 */
public interface Calculus {

    /**
     * Produces {@link XML} representing metrics values.
     * @param metric Desired metric to calculate
     * @param params Params
     * @param skeleton Package input
     * @return XML document giving metrics values for classes
     * @throws IOException If fails
     */
    XML node(String metric, Map<String, Object> params, XML skeleton)
        throws IOException;

}
