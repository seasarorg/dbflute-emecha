/*
 * Copyright 2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DFPropPartitions;

/**
 * dfpropのパーティショニングを行うクラス。
 */
public class DFPropPartitionScanner extends RuleBasedPartitionScanner implements DFPropPartitions {

    public DFPropPartitionScanner() {

        IToken dfpComment = new Token(DFP_COMMENT);
        IToken tagPartition = new Token(DFP_PARTITIONING);

        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        rules.add(new SingleLineRule("/*", "*/", tagPartition));
        rules.add(new SingleLineRule("\"", "\"", tagPartition));
        rules.add(new SingleLineRule("'", "'", tagPartition));

        rules.add(new EndOfLineRule("#", dfpComment));

        setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
    }

}
