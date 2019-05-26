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

import org.apache.ant.props.RegexBasedEvaluator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

/**
 * Require property operation.
 */
public class RequireProperty extends RegexBasedEvaluator {
    private static final String DEFAULT_MESSAGE = "Missing required property ";

    /**
     * Construct a new RequireProperty operation.
     */
    public RequireProperty() {
        super("^(.*):\\?(.*)$");
    }

    /** {@inheritDoc} */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        String result = (String) propertyHelper.getProperty(groups[1]);
        if (result == null) {
            String message = "".equals(groups[2]) ? DEFAULT_MESSAGE + groups[1] : groups[2];
            throw new BuildException(message);
        }
        return result;
    }
}
