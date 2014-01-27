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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColorManager;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.rule.CombinedWordRule;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.rule.WhitespaceDetector;

public class DefaultTokenScanner extends BsDFPropScanner {

    public DefaultTokenScanner(DfColorManager manager, IPreferenceStore store) {
        super(manager, store);
        setDefaultReturnToken(getToken(DfColor.DEFAULT));
    }

    @Override
    protected List<IRule> createRules() {
        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new WhitespaceRule(new WhitespaceDetector()));

        rules.add(new SingleLineRule("$$","$$",getToken(DfColor.ALIAS_MARK)));
        rules.add(new SingleLineRule("/*","*/",getToken(DfColor.SQL)));
        rules.add(new SingleLineRule("\"","\"",getToken(DfColor.VALIABLE)));
        rules.add(new SingleLineRule("'","'",getToken(DfColor.VALIABLE)));

        CombinedWordRule wordRule = new CombinedWordRule();
        CombinedWordRule.WordMatcher mapMacher = new CombinedWordRule.WordMatcher();
        mapMacher.addWord("map:", getToken(DfColor.FIXED_LITERAL_MARK));
        wordRule.addWordMatcher(mapMacher);
        CombinedWordRule.WordMatcher listMacher = new CombinedWordRule.WordMatcher();
        listMacher.addWord("list:", getToken(DfColor.FIXED_LITERAL_MARK));
        wordRule.addWordMatcher(listMacher);

        CombinedWordRule.WordMatcher trueMacher = new CombinedWordRule.WordMatcher();
        listMacher.addWord("true", getToken(DfColor.FIXED_LITERAL_MARK));
        wordRule.addWordMatcher(trueMacher);
        CombinedWordRule.WordMatcher falseMacher = new CombinedWordRule.WordMatcher();
        listMacher.addWord("false", getToken(DfColor.FIXED_LITERAL_MARK));
        wordRule.addWordMatcher(falseMacher);

        CombinedWordRule.WordMatcher suffixMacher = new CombinedWordRule.WordMatcher();
        suffixMacher.addWord("suffix:", getToken(DfColor.LIKE_SEARCH_MARK));
        wordRule.addWordMatcher(suffixMacher);
        CombinedWordRule.WordMatcher prefixMacher = new CombinedWordRule.WordMatcher();
        prefixMacher.addWord("prefix:", getToken(DfColor.LIKE_SEARCH_MARK));
        wordRule.addWordMatcher(prefixMacher);
        CombinedWordRule.WordMatcher containMacher = new CombinedWordRule.WordMatcher();
        containMacher.addWord("contain:", getToken(DfColor.LIKE_SEARCH_MARK));
        wordRule.addWordMatcher(containMacher);

        CombinedWordRule.WordMatcher sqlMacher = new CombinedWordRule.WordMatcher();
        sqlMacher.addWord("sql:", getToken(DfColor.LIKE_SEARCH_MARK));
        wordRule.addWordMatcher(sqlMacher);

        rules.add(wordRule);

        return rules;
    }

}
