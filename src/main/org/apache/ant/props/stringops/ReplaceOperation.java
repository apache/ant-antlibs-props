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
package org.apache.ant.props.stringops;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * Replace operation--handles 'replace all' and 'replace first' alike.
 */
public class ReplaceOperation extends PatternOperation {
    private static final String ESCAPE_SLASH = "\\\\/";
    private static final String NOT_SLASH = "[^/]";
    private static final String WORD = "(?:" + ESCAPE_SLASH + "|" + NOT_SLASH + ")*";
    private static final String RE = "^(" + WORD + ")(//?)(" + WORD + ")/(" + WORD + ")$";

    /**
     * Construct a new ReplaceOperation.
     */
    protected ReplaceOperation() {
        super(RE);
    }

    /** {@inheritDoc} */
    protected final Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        Object value = propertyHelper.getProperty(groups[1]);
        int replaceOption = "//".equals(groups[2]) ? Regexp.REPLACE_ALL : Regexp.REPLACE_FIRST;
        StringBuffer sb = new StringBuffer(groups[3]);
        convertToRegex(sb);
        return value == null ? null : createRegexp(sb.toString(), propertyHelper.getProject())
                .substitute(value.toString(), groups[4], replaceOption);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.ant.props.stringops.PatternOperation#convertToRegex(java.lang.StringBuffer)
     */
    protected void convertToRegex(StringBuffer sb) {
        deEscape('/', sb);
        super.convertToRegex(sb);
    }

}
