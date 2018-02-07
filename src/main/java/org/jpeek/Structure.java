/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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

import com.jcabi.xml.XML;
import java.io.IOException;

/**
 * Structure of the classes in XML.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.25
 * @todo #100:30min Add more implementations of Structure which could be
 *  decorators over other types of Structure. We currently have the Skeleton,
 *  which provides an XML structure common for all the classes, but we may want
 *  to obtain more information about the classes' structure, depending on
 *  the Report. Because of this, the Report/Structure/XSL stylesheets should
 *  work in a composable manner. A first Structure decorator could fetch the
 *  information required by the original ticket (#100). Then, others should
 *  come, depending on the need.
 */
public interface Structure {

    /**
     * As XMl.
     * @return XML structure.
     * @throws IOException If something goes wrong.
     */
    XML xml() throws IOException;
}
