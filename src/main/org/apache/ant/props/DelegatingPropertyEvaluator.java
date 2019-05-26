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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.PropertyHelper.PropertyEvaluator;

/**
 * Abstract delegating {@link PropertyEvaluator}.
 */
public abstract class DelegatingPropertyEvaluator implements PropertyHelper.PropertyEvaluator {
    private final ThreadLocal stack = new ThreadLocal();

    private ArrayList delegates = new ArrayList();

    /**
     * Add a {@link PropertyEvaluator} delegate.
     * @param propertyEvaluator to add
     */
    protected void addDelegate(PropertyEvaluator propertyEvaluator) {
        delegates.add(propertyEvaluator);
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(String propertyName, PropertyHelper propertyHelper) {
        if (getRequiredStack().contains(propertyName)) {
            return null;
        }
        push(propertyName);
        try {
            for (Iterator iter = delegates.iterator(); iter.hasNext();) {
                Object value = ((PropertyEvaluator) iter.next()).evaluate(propertyName,
                        propertyHelper);
                if (value != null) {
                    return value;
                }
            }
        } finally {
            pop();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DelegatingPropertyEvaluator == false) {
            return false;
        }
        return delegates.equals(((DelegatingPropertyEvaluator) obj).delegates);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 17 * delegates.hashCode();
    }

    private synchronized Stack getRequiredStack() {
        Stack result = (Stack) stack.get();
        if (result == null) {
            result = new Stack();
            stack.set(result);
        }
        return result;
    }

    private synchronized void push(String propertyName) {
        getRequiredStack().push(propertyName);
    }

    private synchronized void pop() {
        Stack stk = (Stack) stack.get();
        if (stk != null) {
            stk.pop();
            if (stk.isEmpty()) {
                stack.set(null);
            }
        }
    }
}
