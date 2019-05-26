/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.ant.props;

import org.apache.tools.ant.PropertyHelper;

/**
 * Abstract prefixed PropertyEvaluator.
 */
public abstract class PrefixedEvaluator extends RegexBasedEvaluator {

    /**
     * Default prefix delimiter.
     */
    public static final String DEFAULT_DELIMITER = ":";

    /**
     * Create a new PrefixedEvaluator.
     */
    protected PrefixedEvaluator() {
        this(DEFAULT_DELIMITER);
    }

    /**
     * Create a new PrefixedEvaluator.
     * 
     * @param delimiter
     */
    protected PrefixedEvaluator(String delimiter) {
        setDelimiter(delimiter);
    }

    /**
     * Learn whether this evaluator can interpret a property with the given prefix.
     * 
     * @param prefix
     * @return <code>true</code> if <code>prefix</code> is recognized, else <code>false</code>.
     */
    protected abstract boolean canInterpret(String prefix);

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.ant.props.RegexBasedEvaluator#evaluate(java.lang.String[],
     *      org.apache.tools.ant.PropertyHelper)
     */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        return canInterpret(groups[1]) ? evaluate(groups[2], groups[1], propertyHelper) : null;
    }

    /**
     * Return the result of evaluating the prefixed property.
     * 
     * @param property
     * @param prefix
     * @param propertyHelper
     * @return Object if the property can be resolved, else <code>null</code>.
     */
    protected abstract Object evaluate(String property, String prefix, PropertyHelper propertyHelper);

    /**
     * Set the String delimiter.
     * 
     * @param delimiter String
     */
    public void setDelimiter(String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("invalid delimiter: null");
        }
        super.setPattern("^(.*?)" + delimiter + "(.*)$");
    }

    /**
     * Ignored.
     * 
     * @see org.apache.ant.props.RegexBasedEvaluator#setPattern(java.lang.String)
     */
    public final void setPattern(String pattern) {
    }
}
