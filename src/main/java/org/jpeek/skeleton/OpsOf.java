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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;

/**
 * Operators of the method.
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class OpsOf extends MethodVisitor {

    /**
     * Dirs.
     */
    private final Directives target;

    /**
     * Ctor.
     * @param dirs Directives
     * @param origin Original visitor
     */
    OpsOf(final Directives dirs, final MethodVisitor origin) {
        super(Opcodes.ASM6, origin);
        this.target = dirs;
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
        final String attr, final String dsc) {
        super.visitFieldInsn(opcode, owner, attr, dsc);
        this.target.addIf("ops").add("op");
        if (opcode == Opcodes.GETFIELD) {
            this.target.attr("code", "get");
        } else if (opcode == Opcodes.PUTFIELD) {
            this.target.attr("code", "put");
        } else if (opcode == Opcodes.GETSTATIC) {
            this.target.attr("code", "get_static");
        } else if (opcode == Opcodes.PUTSTATIC) {
            this.target.attr("code", "put_static");
        }
        this.target.set(attr).up().up();
    }

    @Override
    public void visitMethodInsn(final int opcode,
        final String owner, final String mtd,
        final String dsc, final boolean itf) {
        super.visitMethodInsn(opcode, owner, mtd, dsc, itf);
        this.target.strict(1).addIf("ops").add("op");
        this.target
            .attr("code", "call")
            .set(owner.replace("/", ".").concat(".").concat(mtd))
            .up().up();
    }

}
