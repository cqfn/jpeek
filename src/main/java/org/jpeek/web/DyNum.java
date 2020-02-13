/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle JavadocTagsCheck (500 lines)
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
