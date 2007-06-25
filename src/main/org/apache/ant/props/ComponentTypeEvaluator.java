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
package org.apache.ant.props;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

/**
 * Property evaluator that will map any colon-delimited currently defined Ant type
 * using its String constructor, if it has any.
 */
public class ComponentTypeEvaluator implements PropertyHelper.PropertyEvaluator {
    private static final String DEFAULT_DELIMITER = ":";

    private static final Class[] PROJECT_STRING = new Class[] { Project.class,
            String.class };

    private static final Class[] STRING_ONLY = new Class[] { String.class };

    private String delimiter;

    /**
     * {@inheritDoc}
     * @see org.apache.tools.ant.PropertyHelper.PropertyEvaluator#evaluate(java.lang.String, org.apache.tools.ant.PropertyHelper)
     */
    public Object evaluate(String property, PropertyHelper propertyHelper) {
        int d = property.indexOf(getDelimiter());
        Object result = null;
        if (d >= 0) {
            Project p = propertyHelper.getProject();
            Class componentType = ComponentHelper.getComponentHelper(p)
                    .getDefinition(property.substring(0, d)).getTypeClass(p);
            if (componentType != null) {
                String stringArg = property.substring(d + 1);
                try {
                    result = componentType.getConstructor(PROJECT_STRING)
                            .newInstance(new Object[] { p, stringArg });
                } catch (Exception e) {
                }
                try {
                    result = componentType.getConstructor(STRING_ONLY)
                            .newInstance(new Object[] { stringArg });
                } catch (Exception e) {
                }
                if (result != null) {
                    p.setProjectReference(result);
                }
            }
        }
        return result;
    }

    /**
     * Get the String delimiter.
     * @return String
     */
    public String getDelimiter() {
        return delimiter == null ? DEFAULT_DELIMITER : delimiter;
    }

    /**
     * Set the String delimiter.
     * @param delimiter String
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
