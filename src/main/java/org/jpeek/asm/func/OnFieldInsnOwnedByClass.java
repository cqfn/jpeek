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

import org.objectweb.asm.Type;

/**
 * Procedure that will be executed on visiting JVM instruction
 * to access field inside method. Function reacts only to
 * fields that owned by class of calling method.
 *
 * <p>Class may contain several methods where inside of them you access
 * to fields of this one class and to fields of another classes.
 * Use this one procedure to filter all access field instructions inside
 * methods and to react only on instructions that access fields owned by
 * this one class.</p>
 *
 * @author Sergey Karazhenets (sergeykarazhenets@gmail.com)
 * @version $Id$
 * @since 0.13
 */
public final class OnFieldInsnOwnedByClass implements
    Proc4<Integer, String, String, String> {

    /**
     * Name of the class.
     */
    private final String classname;

    /**
     * Procedure that will be executed when method visitor will visit
     * access field instruction for field owned by class of calling method.
     */
    private final Proc4<Integer, String, String, String> proc;

    /**
     * Ctor.
     *
     * @param classname Name of the class.
     * @param proc Procedure to execute on field instruction visit event
     *  for field owned by class of calling method.
     */
    public OnFieldInsnOwnedByClass(
        final String classname,
        final Proc4<Integer, String, String, String> proc
    ) {
        this.classname = classname;
        this.proc = proc;
    }

    @Override
    // @checkstyle ParameterNumberCheck (1 line)
    public void exec(final Integer opcode, final String owner,
        final String name, final String desc
    ) throws Exception {
        final String ownerclass = Type.getObjectType(owner).getClassName();
        if (ownerclass.equals(this.classname)) {
            this.proc.exec(opcode, owner, name, desc);
        }
    }
}
