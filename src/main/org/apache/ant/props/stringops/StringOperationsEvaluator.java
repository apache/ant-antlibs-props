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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.PropertyHelper.PropertyEvaluator;

/**
 * PropertyEvaluator to apply *nix-style string operations to Ant properties.
 */
public class StringOperationsEvaluator implements PropertyHelper.PropertyEvaluator {
    private static final ThreadLocal STACK = new ThreadLocal() {
        protected Object initialValue() {
            return new Stack();
        }
    };

    private ArrayList delegates = new ArrayList();

    /**
     * Construct a new StringOperationsEvaluator.
     */
    public StringOperationsEvaluator() {
        delegates.add(new Substring());
        delegates.add(new DefaultValue());
        delegates.add(new SetDefaultValue());
        delegates.add(new Translate());
        delegates.add(new RequireProperty());
        delegates.add(new DeleteFromStartGreedy());
        delegates.add(new DeleteFromStartReluctant());
        delegates.add(new DeleteFromEndGreedy());
        delegates.add(new DeleteFromEndReluctant());
        delegates.add(new ReplaceOperation());
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(String propertyName, PropertyHelper propertyHelper) {
        Stack stk = (Stack) STACK.get();
        if (stk.contains(propertyName)) {
            return null;
        }
        stk.push(propertyName);
        try {
            for (Iterator iter = delegates.iterator(); iter.hasNext();) {
                Object value = ((PropertyEvaluator) iter.next()).evaluate(propertyName,
                        propertyHelper);
                if (value != null) {
                    return value;
                }
            }
        } finally {
            if (stk.pop() != propertyName) {
                throw new IllegalStateException("stack out of balance");
            }
        }
        return null;
    }
}
