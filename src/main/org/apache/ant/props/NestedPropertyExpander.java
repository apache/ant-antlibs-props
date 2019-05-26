/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ant.props;

import java.text.ParsePosition;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.property.ParseNextProperty;
import org.apache.tools.ant.property.PropertyExpander;

/**
 * By popular demand:  Nested property expander.
 */
public class NestedPropertyExpander implements PropertyExpander {
    private static final NestedPropertyExpander INSTANCE = new NestedPropertyExpander();

    /**
     * Parse the next property name.
     * @param value the String to parse.
     * @param pos the ParsePosition in use.
     * @param parseNextProperty parse next property
     * @return parsed String if any, else <code>null</code>.
     */
    public String parsePropertyName(String value, ParsePosition pos,
            ParseNextProperty parseNextProperty) {
        int start = pos.getIndex();
        if (value.length() - start >= 3
            && '$' == value.charAt(start) && '{' == value.charAt(start + 1)) {
            parseNextProperty.getProject().log("Attempting nested property processing",
                    Project.MSG_DEBUG);
            pos.setIndex(start + 2);
            StringBuffer sb = new StringBuffer();
            for (int c = pos.getIndex(); c < value.length(); c = pos.getIndex()) {
                if (value.charAt(c) == '}') {
                    pos.setIndex(c + 1);
                    return sb.toString();
                }
                Object o = parseNextProperty.parseNextProperty(value, pos);
                if (o != null) {
                    sb.append(o);
                } else {
                    // be aware that the parse position may now have changed;
                    // update:
                    c = pos.getIndex();
                    sb.append(value.charAt(c));
                    pos.setIndex(c + 1);
                }
            }
        }
        pos.setIndex(start);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return obj == this || obj instanceof NestedPropertyExpander && obj.hashCode() == hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (NestedPropertyExpander.class.equals(getClass())) {
            return System.identityHashCode(INSTANCE);
        }
        throw new UnsupportedOperationException("Get your own hashCode implementation!");
    }
}
