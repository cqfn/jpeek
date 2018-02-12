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

import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.MapEntry;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Classes into XML.
 *
 * <p>We take into account only classes. Interfaces are ignored.</p>
 *
 * <p>There is no thread-safety guarantee.</p>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.23
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings(
    {
        "PMD.AvoidDuplicateLiterals",
        "PMD.ExcessiveImports",
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity",
        "PMD.ModifiedCyclomaticComplexity",
        "PMD.TooManyMethods"
    }
)
final class Skeleton {

    /**
     * XSD schema.
     */
    private static final XSD SCHEMA = XSDDocument.make(
        Report.class.getResourceAsStream("xsd/skeleton.xsd")
    );

    /**
     * The base.
     */
    private final Base base;

    /**
     * Javassist pool.
     */
    private final ClassPool pool;

    /**
     * Ctor.
     * @param bse The base
     */
    Skeleton(final Base bse) {
        this.base = bse;
        this.pool = new ClassPool();
    }

    /**
     * As XML.
     * @return XML structure.
     * @throws IOException If something goes wrong.
     */
    public XML xml() throws IOException {
        return new StrictXML(
            new XMLDocument(
                new Xembler(
                    new Directives()
                        .add("skeleton")
                        .append(new Header())
                        .add("app")
                        .attr("id", this.base)
                        .append(
                            new Joined<Directive>(
                                new Mapped<>(
                                    ent -> new Directives()
                                        .add("package")
                                        .attr("id", ent.getKey())
                                        .append(ent.getValue())
                                        .up(),
                                    this.packages()
                                )
                            )
                        )
                ).xmlQuietly()
            ),
            Skeleton.SCHEMA
        );
    }

    /**
     * Calculate Xembly for all packages.
     * @return XML for all packages (one by one)
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Iterable<Map.Entry<String, Directives>> packages()
        throws IOException {
        final Map<String, Directives> map = new HashMap<>(0);
        final Iterable<Map.Entry<String, Directives>> all = new Mapped<>(
            Skeleton::xembly,
            new Filtered<>(
                // @checkstyle BooleanExpressionComplexityCheck (10 lines)
                ctClass -> !ctClass.isInterface()
                    && !ctClass.isEnum()
                    && !ctClass.isAnnotation()
                    && !ctClass.getName().matches("^.+\\$[0-9]+$")
                    && !ctClass.getName().matches("^.+\\$AjcClosure[0-9]+$"),
                new Mapped<>(
                    path -> {
                        try (InputStream stream =
                            new FileInputStream(path.toFile())) {
                            return this.pool.makeClassIfNew(stream);
                        }
                    },
                    new Filtered<>(
                        path -> Files.isRegularFile(path)
                            && path.toString().endsWith(".class"),
                        this.base.files()
                    )
                )
            )
        );
        for (final Map.Entry<String, Directives> ent : all) {
            map.putIfAbsent(ent.getKey(), new Directives());
            map.get(ent.getKey()).append(ent.getValue());
        }
        return map.entrySet();
    }

    /**
     * Calculate Xembly for a single .class file.
     * @param ctc The class
     * @return Metrics
     */
    private static Map.Entry<String, Directives> xembly(final CtClass ctc) {
        ctc.defrost();
        String pkg = ctc.getPackageName();
        if (pkg == null) {
            pkg = "";
        }
        return new MapEntry<>(
            pkg,
            new Directives()
                .add("class")
                .attr("id", ctc.getSimpleName())
                .append(Skeleton.details(ctc))
                .up()
        );
    }

    /**
     * Turn class into XML.
     * @param ctc The class
     * @return XML
     * @checkstyle ParameterNumberCheck (200 lines)
     * @checkstyle AnonInnerLengthCheck (200 lines)
     */
    private static Iterable<Directive> details(final CtClass ctc) {
        final ClassReader reader;
        try {
            reader = new ClassReader(ctc.toBytecode());
        } catch (final IOException | CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        final Directives dirs = new Directives();
        reader.accept(new Skeleton.Visitor(dirs), 0);
        return dirs;
    }

    /**
     * Class visitor.
     */
    private static final class Visitor extends ClassVisitor {
        /**
         * Dirs.
         */
        private final Directives dirs;
        /**
         * Ctor.
         * @param drs Directives
         */
        Visitor(final Directives drs) {
            super(Opcodes.ASM6);
            this.dirs = drs;
        }
        @Override
        public FieldVisitor visitField(final int access,
            final String name, final String desc,
            final String signature, final Object value) {
            this.dirs.addIf("attributes")
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
                .up().up();
            return super.visitField(
                access, name, desc, signature, value
            );
        }
        @Override
        @SuppressWarnings(
            {
                "PMD.UseVarargs",
                "PMD.UseObjectForClearerAPI",
                "PMD.ExcessiveMethodLength"
            }
        )
        public MethodVisitor visitMethod(final int access,
            final String mtd, final String desc,
            final String signature, final String[] exceptions) {
            this.dirs.addIf("methods")
                .add("method")
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
                );
            final Collection<String> types = new LinkedList<>();
            final AtomicBoolean ret = new AtomicBoolean();
            new SignatureReader(desc).accept(
                new SignatureVisitor(Opcodes.ASM6) {
                    @Override
                    public SignatureVisitor visitReturnType() {
                        ret.set(true);
                        return super.visitReturnType();
                    }
                    @Override
                    public void visitClassType(final String name) {
                        super.visitClassType(name);
                        final String type = String.format("L%s", name);
                        if (ret.compareAndSet(true, false)) {
                            Skeleton.Visitor.this.dirs.add("return")
                                .set(type).up();
                        } else {
                            types.add(type);
                        }
                    }
                    @Override
                    public void visitBaseType(final char name) {
                        super.visitBaseType(name);
                        final String type = String.format("%s", name);
                        if (ret.compareAndSet(true, false)) {
                            Skeleton.Visitor.this.dirs.add("return")
                                .set(type).up();
                        } else {
                            types.add(type);
                        }
                    }
                }
            );
            this.dirs.add("args");
            for (final String type : types) {
                this.dirs.add("arg").set("?").attr("type", type).up();
            }
            this.dirs.up().up().up();
            return new MethodVisitor(
                Opcodes.ASM6,
                super.visitMethod(access, mtd, desc, signature, exceptions)
            ) {
                @Override
                public void visitFieldInsn(final int opcode,
                    final String owner, final String attr,
                    final String dsc) {
                    super.visitFieldInsn(opcode, owner, attr, dsc);
                    Skeleton.Visitor.this.dirs.xpath(
                        String.format(
                            "methods/method[@name='%s' and @desc='%s']",
                            mtd, desc
                        )
                    ).strict(1).addIf("ops").add("op");
                    if (opcode == Opcodes.GETFIELD) {
                        Skeleton.Visitor.this.dirs.attr("code", "get");
                    } else if (opcode == Opcodes.PUTFIELD) {
                        Skeleton.Visitor.this.dirs.attr("code", "put");
                    } else if (opcode == Opcodes.GETSTATIC) {
                        Skeleton.Visitor.this.dirs.attr("code", "get_static");
                    } else if (opcode == Opcodes.PUTSTATIC) {
                        Skeleton.Visitor.this.dirs.attr("code", "put_static");
                    }
                    Skeleton.Visitor.this.dirs.set(attr).up().up().up().up();
                }

                @Override
                public void visitMethodInsn(final int opcode,
                    final String owner, final String name,
                    final String dsc, final boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    Skeleton.Visitor.this.dirs.xpath(
                        String.format(
                            "methods/method[@name='%s' and @desc='%s']",
                            mtd, desc
                        )
                    ).strict(1).addIf("ops").add("op");
                    Skeleton.Visitor.this.dirs
                        .attr("code", "call")
                        .set(owner.replace("/", ".").concat(".").concat(name))
                        .up().up().up().up();
                }
            };
        }
    }
}
