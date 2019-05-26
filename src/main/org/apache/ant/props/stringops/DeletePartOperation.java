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
 * DeletePartOperation to be subclassed for Delete[Start|End][Greedy|Reluctant]..
 */
public abstract class DeletePartOperation extends PatternOperation {
    protected static final boolean GREEDY = true;
    protected static final boolean RELUCTANT = false;

    private boolean greedy;

    /**
     * Construct a new DeletePartOperation.
     */
    protected DeletePartOperation(String pattern, boolean greedy) {
        super(pattern);
        this.greedy = greedy;
    }

    /** {@inheritDoc} */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        Object value = propertyHelper.getProperty(groups[1]);
        if (value != null) {
            String s = value.toString();
            StringBuffer sb = new StringBuffer(groups[2]);
            convertToRegex(sb, greedy);
            String specialized = specializePattern(sb.toString());
            Regexp regexp = createRegexp(specialized, propertyHelper.getProject());
            String result = regexp.substitute(s, "\\1", Regexp.REPLACE_FIRST);
            return result;
        }
        return null;
    }

    /**
     * Specialize the RE pattern for the purposes of the specific subclass.
     * @param pattern partial RE pattern.
     * @return pattern including one group.
     */
    protected abstract String specializePattern(String pattern);

}
