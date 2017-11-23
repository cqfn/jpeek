/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package org.jpeek.asm.func;

/**
 * Procedure of five arguments.
 *
 * <p>If you don't want to have any checked exceptions being thrown
 * out of your {@link Proc5}, you can use {@link UncheckedProc5}
 * decorator.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @param <T> Type of the first argument.
 * @param <R> Type of the second argument.
 * @param <S> Type of the third argument.
 * @param <X> Type of the fourth argument.
 * @param <Y> Type of the fifth argument.
 * @see UncheckedProc5
 * @since 0.13
 */
public interface Proc5<T, R, S, X, Y> {

    /**
     * Execute it.
     *
     * @param first The first argument.
     * @param second The second argument.
     * @param third The third argument.
     * @param fourth The fourth argument.
     * @param fifth The fifth argument.
     * @throws Exception If fails.
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    void exec(T first, R second, S third, X fourth, Y fifth) throws Exception;
}
