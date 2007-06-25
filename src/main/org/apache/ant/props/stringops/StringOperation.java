/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.ant.props.stringops;

import java.util.List;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * String operation based on a regular expression.
 */
public abstract class StringOperation implements PropertyHelper.PropertyEvaluator {
    private ThreadLocal preventRecursion = new ThreadLocal();
    private RegularExpression regularExpression;

    /**
     * Construct a new StringOperation.
     * @param pattern the base pattern.
     */
    protected StringOperation(String pattern) {
        regularExpression = new RegularExpression();
        regularExpression.setPattern(pattern);
    }

    /** {@inheritDoc} */
    public Object evaluate(String propertyName, PropertyHelper propertyHelper) {
        if (preventRecursion.get() != null) {
            return null;
        }
        preventRecursion.set(this);
        try {
            Regexp regexp = regularExpression.getRegexp(propertyHelper.getProject());
            if (regexp.matches(propertyName)) {
                List groups = regexp.getGroups(propertyName, Regexp.MATCH_DEFAULT);
                String[] s = (String[]) groups.toArray(new String[groups.size()]);
                return evaluate(s, propertyHelper);
            }
            return null;
        } finally {
            preventRecursion.set(null);
        }
    }

    /**
     * Evaluate the matched groups.
     * @param groups the matches from the base regex.
     * @param propertyHelper the calling PropertyHelper.
     */
    protected abstract String evaluate(String[] groups, PropertyHelper propertyHelper);
}
