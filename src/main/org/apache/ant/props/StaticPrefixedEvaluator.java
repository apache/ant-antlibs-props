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

/**
 * PrefixedPropertyEvaluator that always uses the same prefix.
 */
public abstract class StaticPrefixedEvaluator extends PrefixedEvaluator {
    private String prefix;

    /**
     * Create a new StaticPrefixedEvaluator.
     */
    protected StaticPrefixedEvaluator() {
    }

    /**
     * Create a new StaticPrefixedEvaluator.
     * @param prefix
     */
    protected StaticPrefixedEvaluator(String prefix) {
        setPrefix(prefix);
    }

    /**
     * Create a new StaticPrefixedEvaluator.
     * @param prefix
     * @param delimiter
     */
    protected StaticPrefixedEvaluator(String prefix, String delimiter) {
        super(delimiter);
        setPrefix(prefix);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.ant.props.PrefixedEvaluator#canInterpret(java.lang.String)
     */
    protected final boolean canInterpret(String prefix) {
        return getRequiredPrefix().equals(prefix);
    }

    /**
     * Get the non-null prefix.
     * @return String
     */
    protected String getRequiredPrefix() {
        String result = getPrefix();
        if (result == null) {
            throw new IllegalStateException("prefix unset");
        }
        return result;
    }

    /**
     * Get the String prefix.
     * @return String
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the String prefix.
     * @param prefix String
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
