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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * Property evaluator that evaluates Ant conditions to a Boolean
 * instance matching the condition's outcome.
 *
 * <p>Default syntax is
 * <code>[!]<em>condition</em>(<em>attribute</em>=<em>value</em>)</code>,
 * for example <code>os(family=unix)</code> or <code>!os(family=unix)</code>.
 */
public class ConditionTypeEvaluator extends RegexBasedEvaluator {
    private static final Pattern COMMA = Pattern.compile(",");
    private static final Pattern EQ = Pattern.compile("=");

    private static final String ASSIGN_ATTR = "" //
            + "(?:.+?)" // reluctant one or more !=
            + "=" // equals
            + "(?:.+?)" // reluctant one or more chars
    ;
    private static final String PATTERN = "" //
            + "^" // beginning
            + "(!)?" // optional bang implying NOT, capturing group 1
            + "(.+?)" // reluctant one-or-more characters for condition name
            + "\\(" // LPAREN
            + "(" // capturing group 2, attribute assignments
            + "(?:" // open nc group ASSIGN_ATTR 1
            + ASSIGN_ATTR //
            + ")" // end nc group ASSIGN_ATTR 1
            + "(?:" // open nc group ASSIGN_ATTR 2..N
            + "," // delimiting comma
            + ASSIGN_ATTR //
            + ")*" // 0..N occurrences
            + ")" // end nc group ASSIGN_ATTR 2..N
            + "\\)" // RPAREN
            + "$" // EOF
    ;

    /**
     * Create a new ConditionTypeEvaluator instance.
     */
    public ConditionTypeEvaluator() {
        super(PATTERN);
    }

    /**
     * {@inheritDoc}
     */
    protected Object evaluate(String[] groups, PropertyHelper propertyHelper) {
        Project p = propertyHelper.getProject();
        boolean negate = false;
        if ("!".equals(groups[1])) {
            negate = true;
        }
        Condition cond = createCondition(p, groups[2]);
        if (cond != null) {
            if (groups[3].length() > 0) {
                Object realObject = TypeAdapter.class.isInstance(cond) ? ((TypeAdapter) cond)
                        .getProxy() : cond;
                if (realObject == null) {
                    throw new IllegalStateException(
                            "Found null proxy object for adapted condition " + cond.toString());
                }
                IntrospectionHelper ih = IntrospectionHelper.getHelper(realObject.getClass());
                String[] attributes = COMMA.split(groups[3]);
                for (int i = 0; i < attributes.length; i++) {
                    String[] keyValue = EQ.split(attributes[i]);
                    ih.setAttribute(p, realObject, keyValue[0].trim(), keyValue[1].trim());
                }
            }
            return Boolean.valueOf(cond.eval() ^ negate);
        }
        return null;
    }

    private Condition createCondition(Project project, String type) {
        Condition result = null;
        ComponentHelper componentHelper = ComponentHelper.getComponentHelper(project);
        Object o = componentHelper.createComponent(type);
        if (o instanceof Condition) {
            result = (Condition) o;
        } else {
            List restrictedDefinitions = componentHelper.getRestrictedDefinitions(type);
            for (Iterator iter = restrictedDefinitions.iterator(); iter.hasNext();) {
                AntTypeDefinition typeDefinition = (AntTypeDefinition) iter.next();
                Class exposedClass = typeDefinition.getExposedClass(project);
                if (exposedClass != null && Condition.class.isAssignableFrom(exposedClass)) {
                    try {
                        result = (Condition) typeDefinition.create(project);
                        break;
                    } catch (Exception e) {
                        project.log("Exception creating type " + typeDefinition, e,
                                Project.MSG_WARN);
                    }
                }
            }
        }
        return result;
    }

}
