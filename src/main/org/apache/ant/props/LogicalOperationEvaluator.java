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
 */
package org.apache.ant.props;

import java.util.regex.Pattern;

import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.PropertyHelper.PropertyEvaluator;

/**
 * {@link PropertyEvaluator} that processes combinations of <code>!</code>,
 * <code>&</code> (<code>+</code> is also accepted for XML ease-of-use),
 * <code>^</code> (XOR), and <code>|</code>, in descending priority.
 * Probably only useful when used with {@link NestedPropertyExpander} which, when
 * taken in conjunction with {@link ConditionTypeEvaluator}, will resolve nested
 * conditions to <code>true</code> or <code>false</code>, allowing them to be
 * processed by this little fellow.
 * 
 * Grouping can be accomplished by means of nested property expressions.
 */
public class LogicalOperationEvaluator extends RegexBasedEvaluator {
    private static final String BOOL = "(?:true|false)";
    private static final String NEGATED_BOOL = "!" + BOOL;
    private static final String EXPR = "(?:" + BOOL + "|" + NEGATED_BOOL + ")";
    private static final String AND = "[\\s]*(?:&|\\+)[\\s]*";
    private static final Pattern AND_PATTERN = Pattern.compile(AND);
    private static final String XOR = "[\\s]*\\^[\\s]*";
    private static final Pattern XOR_PATTERN = Pattern.compile(XOR);
    private static final String OR = "[\\s]*\\|[\\s]*";
    private static final Pattern OR_PATTERN = Pattern.compile(OR);
    private static final String OP = "(?:" + AND + "|" + XOR + "|" + OR + ")" + EXPR;

    // we accept a pattern of either EXPR OP EXPR (OP EXPR)* | NEGATED_BOOL (without op)
    private static final String PATTERN = "^" + "(?:" + EXPR + OP + "(?:" + OP + ")*)|"
            + NEGATED_BOOL + "$";

    /**
     * Create a new LogicalOperationEvaluator instance.
     */
    public LogicalOperationEvaluator() {
        super(PATTERN);
    }

    /**
     * {@inheritDoc}
     */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        boolean result = false;
        String[] or = OR_PATTERN.split(groups[0]);
        for (int o = 0; !result && o < or.length; o++) {
            boolean accumXor = false;
            String[] xor = XOR_PATTERN.split(or[o]);
            for (int x = 0; x < xor.length; x++) {
                boolean accumAnd = true;
                String[] and = AND_PATTERN.split(xor[x]);
                for (int a = 0; accumAnd && a < and.length; a++) {
                    boolean negate = false;
                    String expr = and[a];
                    if (expr.charAt(0) == '!') {
                        negate = true;
                        expr = expr.substring(1);
                    }
                    boolean b = Boolean.valueOf(expr.trim().toLowerCase()).booleanValue() ^ negate;
                    accumAnd &= b;
                }
                accumXor ^= accumAnd;
            }
            result |= accumXor;
        }
        return Boolean.valueOf(result);
    }
}
