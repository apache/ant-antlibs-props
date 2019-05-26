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

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

/**
 * Property evaluator that will map a currently defined Ant type using its String constructor, if it
 * has any.  Default syntax is <code><em>type</em>(<em>arg</em>)</code>.
 */
public class ComponentTypeEvaluator extends RegexBasedEvaluator {
    private static final Class[] PROJECT_STRING = new Class[] { Project.class, String.class };

    private static final Class[] STRING_ONLY = new Class[] { String.class };

    /**
     * Create a new ComponentTypeEvaluator.
     */
    public ComponentTypeEvaluator() {
        super("^(.*?)\\((.*)\\)$");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.ant.props.RegexBasedEvaluator#evaluate(java.lang.String[],
     *      org.apache.tools.ant.PropertyHelper)
     */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        Object result = null;
        Project p = propertyHelper.getProject();
        Class componentType = ComponentHelper.getComponentHelper(p).getDefinition(groups[1])
                .getTypeClass(p);
        if (componentType != null) {
            try {
                result = componentType.getConstructor(PROJECT_STRING).newInstance(
                        new Object[] { p, groups[2] });
            } catch (Exception e) {
            }
            try {
                result = componentType.getConstructor(STRING_ONLY).newInstance(
                        new Object[] { groups[2] });
            } catch (Exception e) {
            }
            if (result != null) {
                p.setProjectReference(result);
            }
        }
        return result;
    }

}
