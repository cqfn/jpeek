/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.skeleton;

import com.jcabi.matchers.XhtmlMatchers;
import org.jpeek.Base;
import org.jpeek.FakeBase;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

/**
 * Test case for {@link Skeleton}.
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 */
final class SkeletonTest {

    @Test
    void createsXml() {
        new Assertion<>(
            "Must overload bar's methods",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("OverloadMethods", "Bar")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "/skeleton/app/package[count(class)=2]",
                "//class[@id='Bar']/methods[count(method)=5]",
                "//class[@id='OverloadMethods']/methods[count(method)=5]",
                "//method[@name='<init>' and @ctor='true']",
                "//class[@id='Bar']//method[@name='getKey']/ops[count(op)=3]",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='put_static' and .='Bar.singleton']",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='call']/name[text() ='java.lang.String.length']",
                "//class[@id='Bar']//method[@name='getKey']/ops/op[@code='get' and .='key']",
                "//class[@id='Bar']//method[@name='<init>']/ops[count(op)=4]"
            )
        ).affirm();
    }

    @Test
    void capturesLocalVariableOps() {
        new Assertion<>(
                "Must capture local variable ops with indexes",
                XhtmlMatchers.xhtml(
                        new Skeleton(
                                new FakeBase("DocDistance")
                        ).xml().toString()
                ),
                XhtmlMatchers.hasXPaths(
                        // @checkstyle LineLength (4 lines)
                        "//class[@id='DocDistance']//method[@name='docThree']/locals/var[@code='store' and @var='1']",
                        "//class[@id='DocDistance']//method[@name='docThree']/locals/var[@code='store' and @var='2']",
                        "//class[@id='DocDistance']//method[@name='docThree']/locals/var[@code='store' and @var='3']",
                        "//class[@id='DocDistance']//method[@name='docThree']/locals/var[@code='store' and @var='4']"
                )
        ).affirm();
    }

    @Test
    void skeletonShouldReflectExactOverloadedCalledMethod() {
        new Assertion<>(
            "Must find arguments of overloaded method",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("OverloadMethods")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (3 lines)
                "//method[@name='methodOne' and @desc='(Ljava/lang/String;)D']/ops/op[@code='call']/name[.='OverloadMethods.methodOne']",
                "//method[@name='methodOne' and @desc='(Ljava/lang/String;)D']/ops/op[@code='call']/args[count(arg)=2]",
                "//method[@name='methodOne' and @desc='(Ljava/lang/String;)D']/ops/op[@code='call']/args/arg[@type='Ljava/lang/String' and .='?']"
            )
        ).affirm();
    }

    @Test
    void findsMethodsAndArgs() {
        new Assertion<>(
            "Must find methods with diff param types",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("MethodsWithDiffParamTypes")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "//class/methods[count(method)=7]",
                "//method[@name='methodSix']/args[count(arg)=1]",
                "//method[@name='methodSix']/args/arg[@type='Ljava/sql/Timestamp']",
                "//method[@name='methodSix' and child::return='Ljava/util/Date']",
                "//method[@name='methodTwo' and child::return='V']",
                "//method[@name='methodOne']/args/arg[@type='Ljava/lang/Object']"
            )
        ).affirm();
    }

    @Test
    void findsMethodCalls() {
        new Assertion<>(
            "Must call methods",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("Bar", "Foo")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (10 lines)
                "//class[@id='Bar']/methods/method[@name='<init>' and @ctor='true']/ops/op/name[text() = 'java.lang.Object.<init>']",
                "//class[@id='Bar']/methods/method[@name='getKey']/ops/op/name[text() = 'java.lang.String.length']",
                "//class[@id='Bar']/methods/method[@name='getValue']/ops/op/name[text() = 'java.lang.String.length']",
                "//class[@id='Bar']/methods/method[@name='setValue']/ops/op/name[text() ='java.lang.UnsupportedOperationException.<init>']",
                "//class[@id='Foo']/methods/method[@name='methodOne']/ops/op/name[text() = 'Foo.methodTwo']",
                "//class[@id='Foo']/methods/method[@name='methodTwo']/ops/op/name[text() = 'Foo.methodOne']"
            )
        ).affirm();
    }

    @Test
    void createsOnlyOneMethodIgnoresSynthetic() {
        new Assertion<>(
            "Must create only one method",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase("OneMethodCreatesLambda")
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (1 line)
                "//class[@id='OneMethodCreatesLambda' and count(methods/method[contains(@name,'doSomething')])=1]"
            )
        ).affirm();
    }

    @Test
    void findFieldWithQualifiedName() {
        new Assertion<>(
            "Must find field with qualified name",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase(
                        "ClassWithPublicField",
                        "ClassAccessingPublicField"
                    )
                )
                    .xml()
                    .toString()
            ),
            XhtmlMatchers.hasXPaths(
                // @checkstyle LineLength (1 line)
                "//class[@id='ClassAccessingPublicField']//method[@name='test']/ops/op[@code='put_static' and .='org.jpeek.samples.ClassWithPublicField.NAME']"
            )
        ).affirm();
    }

    @Test
    void findSchemaOfSkeleton() {
        new Assertion<>(
            "Must find schema of skeleton",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase(
                        "ClassWithDifferentMethodVisibilities"
                    )
                )
                    .xml()
                    .toString()
            ),
            XhtmlMatchers.hasXPaths("//skeleton[@schema='xsd/skeleton.xsd']")
        ).affirm();
    }

    @Test
    void recognizesPublicMethods() {
        new Assertion<>(
            "Must recognize public methods",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new FakeBase(
                        "ClassWithDifferentMethodVisibilities"
                    )
                )
                    .xml()
                    .toString()
            ),
            XhtmlMatchers.hasXPaths(
                "//method[@name='publicMethod' and @visibility='public']",
                "//method[@name='defaultMethod' and @visibility='default']",
                "//method[@name='protectedMethod' and @visibility='protected']",
                "//method[@name='privateMethod' and @visibility='private']"
            )
        ).affirm();
    }

    @Test
    void acceptsSimilarClassNamesInDifferentPackages() {
        new Assertion<>(
            "Must not conflict when class names are identical",
            XhtmlMatchers.xhtml(
                new Skeleton(
                    new Base.Concat(
                        new FakeBase("foo/Foo", "bar/Foo"),
                        new FakeBase("foo/Foo")
                    )
                ).xml().toString()
            ),
            XhtmlMatchers.hasXPaths(
                "/skeleton[count(//class) = 2]",
                "//class[@id='Foo']"
            )
        ).affirm();
    }
}
