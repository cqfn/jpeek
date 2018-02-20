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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javassist.CannotCompileException;
import javassist.CtClass;
import org.cactoos.collection.Joined;
import org.cactoos.collection.Mapped;
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
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle ParameterNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
        super(Opcodes.ASM6);
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
                new Joined<Directive>(
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
    // @todo #139:30min LCOM3: Must first fix #171 by ignoring synthetic
    //  methods. These are constructs generated by the compiler that are
    //  not declared in the source code. After fixing, implement
    //  test case "OneMethodCreatesLambda" for LCOM3.
    //  To ignore synthetic methods, enclose the entire body of this method
    //  within an IF that checks if `access` != Opcode.ACC_SYNTHETIC. As per
    //  docs for ClassVisitor.visitMethod: "This parameter also indicates
    //  if the method is synthetic and/or deprecated."
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
        this.methods.add(dirs);
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
                "public",
                (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC
            )
            .append(new TypesOf(desc));
        return new OpsOf(
            dirs, super.visitMethod(access, mtd, desc, signature, exceptions)
        );
    }

}
