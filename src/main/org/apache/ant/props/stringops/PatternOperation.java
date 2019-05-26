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

import java.text.ParsePosition;

import org.apache.ant.props.RegexBasedEvaluator;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;

/**
 * Abstract pattern-based operation.
 */
public abstract class PatternOperation extends RegexBasedEvaluator {
    private interface PatternParser {
        boolean process(StringBuffer sb, ParsePosition pos);
    }

    private static class RecognizeCrossPlatformSeparators implements PatternParser {
        public boolean process(StringBuffer sb, ParsePosition pos) {
            int index = pos.getIndex();
            int matchLength = sb.indexOf("\\\\", index) == index ? 2
                    : sb.charAt(index) == '/' ? 1 : 0;
            if (matchLength > 0) {
                sb.replace(index, index + matchLength, "[/|\\\\]");
                pos.setIndex(index + 6);
                return true;
            }
            return false;
        }
    }

    private static class PreserveEscapes implements PatternParser {
        public boolean process(StringBuffer sb, ParsePosition pos) {
            int index = pos.getIndex();
            if (sb.charAt(index) == '\\') {
                pos.setIndex(index + 2);
                return true;
            }
            return false;
        }
    }

    private static class CharToPattern implements PatternParser {
        private char patternChar;
        private String regexpPattern;
        CharToPattern(char patternChar, String regexpPattern) {
            this.patternChar = patternChar;
            this.regexpPattern = regexpPattern;
        }
        public boolean process(StringBuffer sb, ParsePosition pos) {
            int index = pos.getIndex();
            if (sb.charAt(index) == patternChar) {
                sb.replace(index, index + 1, regexpPattern);
                pos.setIndex(index + regexpPattern.length());
                return true;
            }
            return false;
        }
    }

    //order is important:
    private static final PatternParser[] GREEDY_PARSERS = new PatternParser[] {
        new RecognizeCrossPlatformSeparators(),
        new PreserveEscapes(),
        new CharToPattern('?', "."),
        new CharToPattern('.', "\\."),
        new CharToPattern('*', ".*")
    };

    private static final PatternParser[] RELUCTANT_PARSERS = new PatternParser[] {
        new RecognizeCrossPlatformSeparators(),
        new PreserveEscapes(),
        new CharToPattern('?', "."),
        new CharToPattern('.', "\\."),
        new CharToPattern('*', ".*?")
    };

    /**
     * Construct a new PatternOperation.
     */
    protected PatternOperation(String regex) {
        super(regex);
    }

    /**
     * Convert a *nix-style pattern to a regex pattern.
     * @param sb StringBuffer to convert.
     */
    protected void convertToRegex(StringBuffer sb) {
        convertToRegex(sb, true);
    }

    /**
     * Convert a *nix-style pattern to a regex pattern.
     * @param sb StringBuffer to convert.
     * @param greedy whether to be greedy.
     */
    protected void convertToRegex(StringBuffer sb, boolean greedy) {
        ParsePosition pos = new ParsePosition(0);
        PatternParser[] parsers = greedy ? GREEDY_PARSERS : RELUCTANT_PARSERS;
nextpos:
        while (pos.getIndex() < sb.length()) {
            for (int i = 0; i < parsers.length; i++) {
                if (parsers[i].process(sb, pos)) {
                    continue nextpos;
                }
            }
            pos.setIndex(pos.getIndex() + 1);
        }
    }

    /**
     * Convert a *nix-style pattern to a regex pattern.
     * @param pattern the *nix-style pattern.
     * @return String regex pattern.
     */
    protected String toRegex(String pattern) {
        return toRegex(pattern, true);
    }

    /**
     * Convert a *nix-style pattern to a regex pattern.
     * @param pattern the *nix-style pattern.
     * @param greedy whether to be greedy.
     * @return String regex pattern.
     */
    protected String toRegex(String pattern, boolean greedy) {
        if (pattern == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(pattern);
        ParsePosition pos = new ParsePosition(0);
        PatternParser[] parsers = greedy ? GREEDY_PARSERS : RELUCTANT_PARSERS;
nextpos:
        while (pos.getIndex() < sb.length()) {
            for (int i = 0; i < parsers.length; i++) {
                if (parsers[i].process(sb, pos)) {
                    continue nextpos;
                }
            }
            pos.setIndex(pos.getIndex() + 1);
        }
        return sb.toString();
    }

    /**
     * Create an Ant Regexp object.
     * @param pattern the regex pattern to use.
     * @param project the associated Project instance.
     * @return Regexp
     */
    protected Regexp createRegexp(String pattern, Project project) {
        RegularExpression re = new RegularExpression();
        re.setPattern(pattern);
        return re.getRegexp(project);
    }

    /**
     * De-escape a given character.
     * @param c the character to "unmask".
     * @param sb the target StringBuffer.
     */
    protected void deEscape(char c, StringBuffer sb) {
        ParsePosition pos = new ParsePosition(0);
        for (int index = pos.getIndex(); index < sb.length(); index = pos.getIndex()) {
            if (sb.charAt(index) == '\\' && sb.charAt(index + 1) == c) {
                sb.deleteCharAt(index);
            }
            pos.setIndex(index + 1);
        }
    }
}
