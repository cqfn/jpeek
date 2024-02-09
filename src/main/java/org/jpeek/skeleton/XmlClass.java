/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Class in XML.
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
final class XmlClass extends ClassVisitor implements Iterable<Directive> {

    /**
     * The class.
     */
    private final CtClass source;

    /**
     * Attributes.
     */
    private final Directives attrs;

    /**
     * Methods.
     */
    private final Collection<Iterable<Directive>> methods;

    /**
     * Ctor.
     * @param src The source
     */
    XmlClass(final CtClass src) {
        super(Opcodes.ASM7);
        this.source = src;
        this.attrs = new Directives();
        this.methods = new LinkedList<>();
    }

    @Override
    public Iterator<Directive> iterator() {
        final ClassReader reader;
        try {
            reader = new ClassReader(this.source.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        this.attrs.add("attributes");
        reader.accept(this, 0);
        return new Directives()
            .append(this.attrs)
            .up()
            .add("methods")
            .append(
                new Joined<>(
                    new Mapped<>(
                        dirs -> new Directives().append(dirs).up(),
                        this.methods
                    )
                )
            )
            .up()
            .iterator();
    }

    @Override
    public FieldVisitor visitField(final int access,
        final String name, final String desc,
        final String signature, final Object value) {
        this.attrs
            .add("attribute")
            .set(name)
            .attr("type", desc.replaceAll(";$", ""))
            .attr(
                "public",
                (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
            )
            .attr(
                "final",
                (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
            )
            .attr(
                "static",
                (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
            )
            .up();
        return super.visitField(access, name, desc, signature, value);
    }

    // @todo #114:30min Find and include the name of the variable the
    //  method is called on, if possible. Currently, only the name of called
    //  method is retrieved. This is currently implemented in
    //  `visitMethodInsn` down below.
    //  Example 1: `Bar.NAME.length();`
    //  - Here, `length` is retrieved from the method `visitMethodInsn`'s
    //  - param `name`, but the word `NAME` itself is not included in any of
    //  - the `visitMethodInsn` arguments.
    //  Example 2: `src.length();`
    //  - Here, `length` is retrieved from the method `visitMethodInsn`'s
    //  - param `name` but the word `src` itself is not included in any of
    //  - the `visitMethodInsn` arguments.
    @Override
    @SuppressWarnings(
        {
            "PMD.UseVarargs",
            "PMD.UseObjectForClearerAPI"
        }
    )
    public MethodVisitor visitMethod(final int access,
        final String mtd, final String desc,
        final String signature, final String[] exceptions) {
        final Directives dirs = new Directives();
        if ((access & Opcodes.ACC_SYNTHETIC) != Opcodes.ACC_SYNTHETIC) {
            String visibility = "default";
            if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                visibility = "public";
            } else if (
                (access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
                visibility = "protected";
            } else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
                visibility = "private";
            }
            dirs.add("method")
                .attr("name", mtd)
                .attr("desc", desc)
                .attr(
                "ctor",
                "<init>".equals(mtd) || "<clinit>".equals(mtd)
                )
                .attr(
                "static",
                (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
                )
                .attr(
                "abstract",
                (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT
                )
                .attr(
                "visibility",
                visibility
                )
                .attr(
                "bridge",
                (access & Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE
                )
                .append(new TypesOf(desc));
            this.methods.add(dirs);
        }
        return new OpsOf(
            dirs, super.visitMethod(access, mtd, desc, signature, exceptions)
        );
    }
}
