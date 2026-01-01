/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 */
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
