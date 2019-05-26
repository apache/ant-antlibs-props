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

import java.util.List;

import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * <code>PropertyHelper.PropertyEvaluator</code> based on a regex pattern that will produce match
 * groups to be dealt with by {@link #evaluate(String[], PropertyHelper)}.
 */
public abstract class RegexBasedEvaluator implements PropertyHelper.PropertyEvaluator {
    private String pattern;

    private RegularExpression regularExpression;
    private int options = Regexp.MATCH_DEFAULT;

    /**
     * Create a new RegexBasedEvaluator.
     */
    protected RegexBasedEvaluator() {
    }

    /**
     * Construct a new RegexBasedEvaluator.
     * 
     * @param pattern the base pattern.
     */
    protected RegexBasedEvaluator(String pattern) {
        setPattern(pattern);
    }
    
    /**
     * Add a matcher option.
     * @param option to add
     */
    protected void addOption(int option) {
        options |= option;
    }

    /** {@inheritDoc} */
    public Object evaluate(String propertyName, PropertyHelper propertyHelper) {
        //never try to resolve the regex factory magic property:
        if (MagicNames.REGEXP_IMPL.equals(propertyName)) {
            return null;
        }
        Regexp regexp = getRegularExpression().getRegexp(propertyHelper.getProject());
        if (regexp.matches(propertyName)) {
            List groups = regexp.getGroups(propertyName, options);
            String[] s = (String[]) groups.toArray(new String[groups.size()]);
            return evaluate(s, propertyHelper);
        }
        return null;
    }

    /**
     * Get the regular expression object to use.
     * 
     * @return the regexp
     */
    protected synchronized RegularExpression getRegularExpression() {
        if (regularExpression == null) {
            if (getPattern() == null) {
                throw new IllegalStateException("pattern not set");
            }
            regularExpression = new RegularExpression();
            regularExpression.setPattern(getPattern());
        }
        return regularExpression;
    }

    /**
     * Evaluate the matched groups.
     * 
     * @param groups the matches from the base regex.
     * @param propertyHelper the calling PropertyHelper.
     */
    protected abstract Object evaluate(String[] groups, PropertyHelper propertyHelper);

    /**
     * Get the String pattern.
     * 
     * @return String
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Set the String pattern.
     * 
     * @param pattern String
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        regularExpression = null;
    }
}
