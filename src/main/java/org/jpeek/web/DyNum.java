/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.jpeek.web;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.jcabi.dynamo.Item;
import java.io.IOException;

/**
 * Number in Dynamo.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.17
 */
final class DyNum extends Number {

    /**
     * Score multiplier.
     */
    private static final Double MULTIPLIER = 100_000.0d;

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -1792658462675142097L;

    /**
     * Number.
     */
    private final double number;

    /**
     * Ctor.
     * @param num The number
     */
    DyNum(final String num) {
        this(Double.parseDouble(num));
    }

    /**
     * Ctor.
     * @param num The number
     */
    DyNum(final long num) {
        this((double) num / DyNum.MULTIPLIER);
    }

    /**
     * Ctor.
     * @param item The item
     * @param attr Attribute name
     * @throws IOException If fails
     */
    DyNum(final Item item, final String attr) throws IOException {
        this(Double.parseDouble(item.get(attr).getN()) / DyNum.MULTIPLIER);
    }

    /**
     * Ctor.
     * @param num The number
     */
    DyNum(final double num) {
        super();
        this.number = num;
    }

    /**
     * Make an update.
     * @return The update
     */
    public AttributeValueUpdate update() {
        return this.update(AttributeAction.PUT);
    }

    /**
     * Make an update.
     * @param action The action
     * @return The update
     */
    public AttributeValueUpdate update(final AttributeAction action) {
        return new AttributeValueUpdate()
            .withAction(action)
            .withValue(
                new AttributeValue().withN(
                    Long.toString(this.longValue())
                )
            );
    }

    @Override
    public int intValue() {
        return (int) this.number;
    }

    @Override
    public long longValue() {
        return (long) (this.number * DyNum.MULTIPLIER);
    }

    @Override
    public float floatValue() {
        return (float) this.number;
    }

    @Override
    public double doubleValue() {
        return this.number;
    }
}
