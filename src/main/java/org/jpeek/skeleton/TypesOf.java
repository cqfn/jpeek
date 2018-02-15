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
package org.jpeek.skeleton;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * List of types in a method signature.
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TypesOf extends SignatureVisitor implements Iterable<Directive> {

    /**
     * Types.
     */
    private final Collection<String> types;

    /**
     * Return type.
     */
    private final AtomicReference<String> rtype;

    /**
     * Method signature.
     */
    private final String singature;

    /**
     * Is it return type?
     */
    private final AtomicBoolean ret;

    /**
     * Ctor.
     * @param sign Method signature
     */
    TypesOf(final String sign) {
        super(Opcodes.ASM6);
        this.types = new LinkedList<>();
        this.singature = sign;
        this.rtype = new AtomicReference<>();
        this.ret = new AtomicBoolean();
    }

    @Override
    public Iterator<Directive> iterator() {
        new SignatureReader(this.singature).accept(this);
        final Directives dirs = new Directives().add("args");
        for (final String type : this.types) {
            dirs.add("arg").set("?").attr("type", type).up();
        }
        dirs.up().add("return").set(this.rtype.get()).up();
        return dirs.iterator();
    }

    @Override
    public SignatureVisitor visitReturnType() {
        this.ret.set(true);
        return super.visitReturnType();
    }

    @Override
    public void visitClassType(final String name) {
        super.visitClassType(name);
        final String type = String.format("L%s", name);
        if (this.ret.compareAndSet(true, false)) {
            this.rtype.set(type);
        } else {
            this.types.add(type);
        }
    }

    @Override
    public void visitBaseType(final char name) {
        super.visitBaseType(name);
        final String type = String.format("%s", name);
        if (this.ret.compareAndSet(true, false)) {
            this.rtype.set(type);
        } else {
            this.types.add(type);
        }
    }

}
