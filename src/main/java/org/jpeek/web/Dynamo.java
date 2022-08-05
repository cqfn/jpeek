/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2022 Yegor Bugayenko
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
package org.jpeek.web;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.jcabi.dynamo.mock.H2Data;
import com.jcabi.dynamo.mock.MkRegion;
import java.io.IOException;
import java.util.Properties;
import org.cactoos.io.ResourceOf;
import org.cactoos.scalar.PropertiesOf;

/**
 * Dynamo.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.14
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class Dynamo implements Region {

    @Override
    public AmazonDynamoDB aws() {
        try {
            return Dynamo.live().aws();
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Table table(final String name) {
        try {
            return Dynamo.live().table(name);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Live DynamoDB table.
     * @return Table
     * @throws IOException If fails
     */
    private static Region live() throws IOException {
        final Properties props = Dynamo.pros();
        final Region reg;
        if (Dynamo.class.getResource("/org/junit/jupiter/api/Test.class") == null) {
            reg = new Region.Simple(
                new Credentials.Simple(
                    props.getProperty("org.jpeek.dynamo.key"),
                    props.getProperty("org.jpeek.dynamo.secret")
                )
            );
        } else {
            reg = new MkRegion(
                new H2Data()
                    .with(
                        "jpeek-results",
                        new String[] {"artifact"},
                        "score", "diff", "ttl", "version", "added",
                        "rank", "good", "classes", "defects", "elements"
                    )
                    .with(
                        "jpeek-mistakes",
                        new String[] {"metric"},
                        "ttl", "avg", "version",
                        "pos", "psum", "pavg",
                        "neg", "nsum", "navg",
                        "champions", "artifact", "mean", "sigma"
                    )
            );
        }
        return reg;
    }

    /**
     * Properties.
     * @return Props
     * @throws IOException If fails
     */
    private static Properties pros() throws IOException {
        return new PropertiesOf(
            new ResourceOf(
                "org/jpeek/jpeek.properties"
            )
        ).value();
    }

}
