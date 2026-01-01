/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import org.cactoos.Text;
import org.cactoos.text.Split;
import org.cactoos.text.Sub;
import org.cactoos.text.TextOf;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;

/**
 * Operators of the method.
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
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
        final Text name;
        if (opcode == Opcodes.GETFIELD) {
            this.target.attr("code", "get");
            name = new TextOf(attr);
        } else if (opcode == Opcodes.PUTFIELD) {
            this.target.attr("code", "put");
            name = new TextOf(attr);
        } else if (opcode == Opcodes.GETSTATIC) {
            this.target.attr("code", "get_static");
            name = new QualifiedName(owner, attr);
        } else if (opcode == Opcodes.PUTSTATIC) {
            this.target.attr("code", "put_static");
            name = new QualifiedName(owner, attr);
        } else {
            name = new TextOf(attr);
        }
        this.target.set(
            name
        ).up().up();
    }

    @Override
    public void visitMethodInsn(final int opcode,
        final String owner, final String mtd,
        final String dsc, final boolean itf) {
        super.visitMethodInsn(opcode, owner, mtd, dsc, itf);
        this.target.strict(1).addIf("ops").add("op");
        this.target
            .attr("code", "call")
            .add("name")
            .set(owner.replace("/", ".").concat(".").concat(mtd))
            .up().add("args");
        final Iterable<Text> args = new Split(
            new Sub(dsc, dsc.indexOf('(') + 1, dsc.indexOf(')')),
            ";"
        );
        for (final Text arg : args) {
            this.target.add("arg").attr("type", arg).set("?").up();
        }
        this.target.up().up().up();
    }
}
