package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
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

		rules.add(new EndOfLineRule("#",getToken(DfColor.LINE_COMMENT)));
		rules.add(new SingleLineRule("$$","$$",getToken(DfColor.ALIAS_MARK)));
		rules.add(new SingleLineRule("/*","*/",getToken(DfColor.SQL)));
		rules.add(new SingleLineRule("\"","\"",getToken(DfColor.VALIABLE)));

		CombinedWordRule wordRule = new CombinedWordRule();
		CombinedWordRule.WordMatcher mapMacher = new CombinedWordRule.WordMatcher();
		mapMacher.addWord("map:", getToken(DfColor.MAP_MARK));
		wordRule.addWordMatcher(mapMacher);
		CombinedWordRule.WordMatcher listMacher = new CombinedWordRule.WordMatcher();
		listMacher.addWord("list:", getToken(DfColor.MAP_MARK));
		wordRule.addWordMatcher(listMacher);

		CombinedWordRule.WordMatcher suffixMacher = new CombinedWordRule.WordMatcher();
		suffixMacher.addWord("suffix:", getToken(DfColor.LIKE_SEARCH_MARK));
		wordRule.addWordMatcher(suffixMacher);
		CombinedWordRule.WordMatcher prefixMacher = new CombinedWordRule.WordMatcher();
		prefixMacher.addWord("prefix:", getToken(DfColor.LIKE_SEARCH_MARK));
		wordRule.addWordMatcher(prefixMacher);
		CombinedWordRule.WordMatcher containMacher = new CombinedWordRule.WordMatcher();
		containMacher.addWord("contain:", getToken(DfColor.LIKE_SEARCH_MARK));
		wordRule.addWordMatcher(containMacher);

		rules.add(wordRule);

		return rules;
	}

}
