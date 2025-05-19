/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * @see <a href="http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf">A packages suite for object oriented design</a>
 * @since 0.27
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
    private final String signature;

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
        this.signature = sign;
        this.rtype = new AtomicReference<>();
        this.ret = new AtomicBoolean();
    }

    @Override
    public Iterator<Directive> iterator() {
        new SignatureReader(this.signature).accept(this);
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
