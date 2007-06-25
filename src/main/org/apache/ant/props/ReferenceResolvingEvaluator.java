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

import org.apache.tools.ant.PropertyHelper;

/**
 * Name says it all, doesn't it?
 */
public class ReferenceResolvingEvaluator implements PropertyHelper.PropertyEvaluator {

    /**
     * {@inheritDoc}
     * @see org.apache.tools.ant.PropertyHelper.PropertyEvaluator#evaluate(java.lang.String, org.apache.tools.ant.PropertyHelper)
     */
    public Object evaluate(String property, PropertyHelper propertyHelper) {
        if (property.startsWith("ref:")) {
            Object o = propertyHelper.getProject().getReference(property.substring(4));
            if (o != null) {
                return o;
            }
        }
        return null;
    }
}
